package com.zzhl.wechat;

import com.github.wxpay.sdk.WXPay;
import com.github.wxpay.sdk.WXPayUtil;
import com.zzhl.config.Constant;
import com.zzhl.config.PayConfig;
import com.zzhl.config.WechatConfig;
import com.zzhl.util.DateUtil;
import com.zzhl.util.JSONUtil;
import com.zzhl.wechat.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

@Service
public class WechatApi {
    private static final Logger logger = LoggerFactory.getLogger(WechatApi.class);

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private WechatConfig wechatConfig;

    @Autowired
    private PayConfig payConfig;

    /**
     * 开发者服务器使用登录凭证 code 获取 session_key 和 openid。session_key 是对用户数据进行加密签名的密钥
     *
     * @param loginCode
     * @return
     */
    public WechatGetSessionKey jscode2session(String loginCode) {
        String url = "https://api.weixin.qq.com/sns/jscode2session?appid=" + wechatConfig.getMiniProgram().getAppId() + "&secret=" + wechatConfig.getMiniProgram().getSecret() + "&grant_type=authorization_code&js_code=" + loginCode;
        ResponseEntity<WechatGetSessionKey> responseEntity = restTemplate.getForEntity(url, WechatGetSessionKey.class);
        return responseEntity.getBody();
    }

    /**
     * 获取 access_token
     */
    public synchronized String getAccessToken() {
        if (!StringUtils.isEmpty(Constant.WX_ACCESS_TOKEN) && Constant.WX_ACCESS_TOKEN_EXPIRES > 0) {
            if (System.currentTimeMillis() < Constant.WX_ACCESS_TOKEN_EXPIRES) {
                return Constant.WX_ACCESS_TOKEN;
            }
        }
        String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=" + wechatConfig.getMiniProgram().getAppId() + "&secret=" + wechatConfig.getMiniProgram().getSecret();
        ResponseEntity<WechatGetAccessToken> responseEntity = restTemplate.getForEntity(url, WechatGetAccessToken.class);
        WechatGetAccessToken wechatGetAccessToken = responseEntity.getBody();
        if (wechatGetAccessToken == null || StringUtils.isEmpty(wechatGetAccessToken.getAccess_token())) {
            logger.error("获取微信access_token失败 response = {}", wechatGetAccessToken == null ? "wechatGetAccessToken is null" : wechatGetAccessToken.toString());
            return null;
        }

        long expires = StringUtils.isEmpty(wechatGetAccessToken.getExpires_in()) ? 3600000L : Long.parseLong(wechatGetAccessToken.getExpires_in()) * 1000L - 1200000L;
        Constant.WX_ACCESS_TOKEN = wechatGetAccessToken.getAccess_token();
        Constant.WX_ACCESS_TOKEN_EXPIRES = System.currentTimeMillis() + expires;

        return wechatGetAccessToken.getAccess_token();
    }

    /**
     * 发送客服消息
     */
    public void sendKfMessage(WechatSendKfMessage message, String accessToken) {
        String url = "https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=" + accessToken;
        logger.debug("客服消息内容 {}", message);
        ResponseEntity<WechatError> responseEntity = restTemplate.postForEntity(url, message, WechatError.class);
        WechatError wechatError = responseEntity.getBody();
        if (wechatError == null || wechatError.getErrcode() != 0) {
            logger.error("发送客服消息失败 response = {}", wechatError == null ? "返回数据为空" : wechatError.toString());
        }
    }

    /**
     * 发送模板消息
     */
    public void sendTemplateMessage(WechatSendTemplateMessage message, String accessToken) {
        String url = "https://api.weixin.qq.com/cgi-bin/message/wxopen/template/send?access_token=" + accessToken;
        logger.debug("模板消息内容 {}", message);
        ResponseEntity<WechatError> responseEntity = restTemplate.postForEntity(url, message, WechatError.class);
        WechatError wechatError = responseEntity.getBody();
        if (wechatError == null || wechatError.getErrcode() != 0) {
            logger.error("发送模板消息失败 response = {}", wechatError == null ? "返回数据为空" : wechatError.toString());
        }
    }

    /**
     * 获取麻将计分器小程序码
     */
    public byte[] getWxacodeUnlimit(String accessToken, String page, String param) {
        OutputStreamWriter out = null;
        HttpURLConnection conn = null;
        byte[] resultData = null;
        try {
            URL httpUrl = new URL("https://api.weixin.qq.com/wxa/getwxacodeunlimit?access_token=" + accessToken);
            conn = (HttpURLConnection) httpUrl.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("connection", "keep-alive");
            conn.setUseCaches(false);//设置不要缓存
            conn.setInstanceFollowRedirects(true);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.connect();
            //POST请求
            Map<String, Object> rgbMap = new HashMap<>();
            rgbMap.put("r", "0");
            rgbMap.put("g", "0");
            rgbMap.put("b", "0");
            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put("scene", param);
            paramMap.put("page", page);
            paramMap.put("width", 430);
            paramMap.put("auto_color", false);
            paramMap.put("line_color", rgbMap);
            out = new OutputStreamWriter(conn.getOutputStream());
            out.write(JSONUtil.bean2Json(paramMap));
            out.flush();

            // 得到输入流获取自己数组
            resultData = readInputStream(conn.getInputStream());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (conn != null) {
                    conn.disconnect();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return resultData;
    }


    /**
     * 生成订单
     */
    public Map<String, String> prePay(String openId, String tradeNo, String body, String totalFee) {
        WXPay wxpay = new WXPay(payConfig);

        Map<String, String> data = new HashMap<String, String>();
        data.put("body", body);
        data.put("out_trade_no", tradeNo);
        data.put("device_info", "");
        data.put("fee_type", "CNY");
        data.put("total_fee", totalFee);
        data.put("spbill_create_ip", wechatConfig.getPay().getSpbillCreateIp());
        data.put("notify_url", wechatConfig.getPay().getNotifyUrl());
        data.put("trade_type", wechatConfig.getPay().getTradeType());  // 此处指定为扫码支付
        data.put("openid", openId);

        try {
            Map<String, String> resp = wxpay.unifiedOrder(data);

            Map<String, String> signData = new HashMap<>();
            signData.put("appId", resp.get("appid"));
            signData.put("timeStamp", String.valueOf(System.currentTimeMillis()));
            signData.put("nonceStr", resp.get("nonce_str"));
            signData.put("package", "prepay_id=" + resp.get("prepay_id"));
            signData.put("signType", "MD5");

            String paySign = WXPayUtil.generateSignature(signData, payConfig.getKey());

            signData.remove("appId");
            signData.put("paySign", paySign);
            signData.put("prePayId", resp.get("prepay_id"));

            return signData;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * @param url
     * @param Params
     * @return
     * @throws IOException
     * @作用 使用urlconnection
     */
    public void downloadFile(String url, String Params) {
        OutputStreamWriter out = null;
        String response = "";
        try {
            URL httpUrl = null; //HTTP URL类 用这个类来创建连接
            //创建URL
            httpUrl = new URL(url);
            //建立连接
            HttpURLConnection conn = (HttpURLConnection) httpUrl.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("connection", "keep-alive");
            conn.setUseCaches(false);//设置不要缓存
            conn.setInstanceFollowRedirects(true);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.connect();
            //POST请求
            out = new OutputStreamWriter(conn.getOutputStream());
            out.write(Params);
            out.flush();

            // 得到输入流获取自己数组
            byte[] getData = readInputStream(conn.getInputStream());

            //文件保存位置
            File saveDir = new File("/Users/yaojunjian/Downloads");
            if(!saveDir.exists()){
                saveDir.mkdir();
            }
            File file = new File(saveDir + File.separator+ "miniprogram.png");
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(getData);
            if(fos!=null){
                fos.close();
            }

            // 断开连接
            conn.disconnect();
        } catch (Exception e) {
            System.out.println("发送 POST 请求出现异常！" + e);
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * 从输入流中获取字节数组
     * @param inputStream
     * @return
     * @throws IOException
     */
    public static byte[] readInputStream(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[1024];
        int len = 0;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while((len = inputStream.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
        bos.close();
        return bos.toByteArray();
    }
}
