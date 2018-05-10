package com.zzhl.controller;

import com.github.wxpay.sdk.WXPay;
import com.github.wxpay.sdk.WXPayUtil;
import com.zzhl.config.PayConfig;
import com.zzhl.domain.pojo.WechatUser;
import com.zzhl.dto.BaseResult;
import com.zzhl.dto.requst.LoginRequestDTO;
import com.zzhl.exception.CodeException;
import com.zzhl.exception.ErrorCode;
import com.zzhl.service.RedEnvelopeService;
import com.zzhl.service.WechatUserService;
import com.zzhl.util.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("wechat")
public class WechatController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(WechatController.class);

    @Autowired
    private WechatUserService wechatUserService;
    @Autowired
    private RedEnvelopeService redEnvelopeService;

    /**
     * 获取微信用户信息
     */
    @RequestMapping(value = "/login")
    public @ResponseBody BaseResult<WechatUser> login(@RequestBody LoginRequestDTO request) {
        if (request == null) {
            throw new CodeException(ErrorCode.PARAMS_ERROR);
        }

        return new BaseResult<>(wechatUserService.login(request));
    }

    /**
     * 用户打开小程序数据统计
     *
     * @return
     */
    @RequestMapping(value = "/log/uv/{uid}")
    public @ResponseBody BaseResult<Void> logUV(@PathVariable("uid") Integer uid) {
        if (NumberUtils.isValidId(uid)) {
            wechatUserService.logUV(uid);
        }
        return new BaseResult<>();
    }

    /**
     * 微信支付结果异步通知
     */
    @RequestMapping(value = "/pay/notify")
    public void notify(HttpServletRequest request, HttpServletResponse response) {
        InputStream inputStream = null;
        OutputStream out = null;
        BufferedReader bufferedReader = null;
        StringBuffer stringBuffer = null;
        Map<String, String> responseMap = new HashMap<>();
        try {
            inputStream = request.getInputStream();
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            stringBuffer = new StringBuffer();
            String line = null;
            while((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line);
            }
            String requestBody = stringBuffer.toString();

            logger.info("wechat pay async notify request data: {}", requestBody);

            WXPay wxpay = new WXPay(new PayConfig());
            Map<String, String> notifyMap = WXPayUtil.xmlToMap(requestBody);
            if (wxpay.isPayResultNotifySignatureValid(notifyMap)) {
                redEnvelopeService.payNotify(notifyMap);
                // 签名正确
                // 进行处理。
                // 注意特殊情况：订单已经退款，但收到了支付结果成功的通知，不应把商户侧订单状态从退款改成支付成功
                responseMap.put("return_code", "SUCCESS");
                responseMap.put("return_msg", "OK");
            }
            else {
                // 签名错误，如果数据里没有sign字段，也认为是签名错误
                responseMap.put("return_code", "FAIL");
                responseMap.put("return_msg", "签名错误");
            }
            logger.info("wechat pay async notify response data: {}", responseMap);
            out = response.getOutputStream();
            out.write(WXPayUtil.mapToXml(responseMap).getBytes());
            out.flush();
        } catch (Exception e) {
            logger.error("wehcat notify error: {}", e.fillInStackTrace());
        } finally {
            if (stringBuffer != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}
