package com.zzhl.service;

import com.zzhl.annotation.RequestLockable;
import com.zzhl.config.RedEnvelopeConfig;
import com.zzhl.config.WechatConfig;
import com.zzhl.domain.mapper.*;
import com.zzhl.domain.pojo.*;
import com.zzhl.dto.requst.Page;
import com.zzhl.dto.response.*;
import com.zzhl.exception.CodeException;
import com.zzhl.exception.ErrorCode;
import com.zzhl.util.DateUtil;
import com.zzhl.util.NumberUtils;
import com.zzhl.util.RedEnvelopesUtil;
import com.zzhl.wechat.WechatApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service("redEnvelopeService")
public class RedEnvelopeService {
    private static final Logger logger = LoggerFactory.getLogger(RedEnvelopeService.class);

    @Autowired
    private WechatUserService wechatUserService;

    @Autowired
    private RedEnvelopeConfig redEnvelopeConfig;
    @Autowired
    private WechatConfig wechatConfig;
    @Autowired
    private WechatApi wechatApi;

    @Autowired
    private RedEnvelopeMapper redEnvelopeMapper;
    @Autowired
    private PayLogMapper payLogMapper;
    @Autowired
    private RedEnvelopeReceiveMapper redEnvelopeReceiveMapper;
    @Autowired
    private RedEnvelopeTemplateMapper redEnvelopeTemplateMapper;
    @Autowired
    private WithdrawalsMapper withdrawalsMapper;

    public List<RedEnvelopeTemplate> listTemplate(Page page) {
        return redEnvelopeTemplateMapper.listByPage(page.getOffset(), page.getLimit());
    }

    /**
     * 获取红包详情
     */
    public DetailResponseDTO getDetail(Integer redEnvelopeId, Integer uid, Integer offset, Integer limit) {
        RedEnvelope redEnvelope = redEnvelopeMapper.selectByPrimaryKey(redEnvelopeId);
        if (redEnvelope == null) {
            throw new CodeException(ErrorCode.PARAMS_ERROR.getState(), "红包不存在");
        }
        DetailResponseDTO responseDTO = new DetailResponseDTO();
        responseDTO.setRedEnvelope(redEnvelope);
        responseDTO.setRedEnvelopeUser(wechatUserService.getUser(redEnvelope.getUid()));

        List<RedEnvelopeReceive> myRedEnvelopes = redEnvelopeReceiveMapper.selectByUidOrReid(uid, redEnvelopeId, 0, 1);
        if (myRedEnvelopes != null && myRedEnvelopes.size() > 0) {
            responseDTO.setMyFee(String.format("%.2f", (double)myRedEnvelopes.get(0).getFee() / 100));
            responseDTO.setReceiveStatus(2);    // 0-未结束，可领取；1-已结束，2-已领取
        } else {
            responseDTO.setReceiveStatus(redEnvelope.getIsFinish().intValue());
        }

        List<RedEnvelopeReceive> redEnvelopes = redEnvelopeReceiveMapper.selectByUidOrReid(null, redEnvelopeId, offset, limit);
        if (redEnvelopes != null && redEnvelopes.size() > 0) {
            List<MyRedEnvelope> list = new ArrayList<>();
            redEnvelopes.forEach(receive -> {
                MyRedEnvelope myRedEnvelope = new MyRedEnvelope();
                myRedEnvelope.setId(receive.getId());
                myRedEnvelope.setFee(receive.getFee() / 100);
                myRedEnvelope.setCreateTime(receive.getCreateTime());
                myRedEnvelope.setRedEnvelopeUser(wechatUserService.getUser(receive.getUid()));
                list.add(myRedEnvelope);
            });
            responseDTO.setRedEnvelopes(list);
        }

        return responseDTO;
    }

    public SendStep1ResponseDTO sendStep1(Integer uid) {
        UserExtra userExtra = wechatUserService.getUserExtra(uid);
        if (userExtra == null) {
            throw new CodeException(ErrorCode.PARAMS_ERROR.getState(), "用户不存在");
        }
        SendStep1ResponseDTO responseDTO = new SendStep1ResponseDTO();
        responseDTO.setBalance(String.format("%.2f", (double)userExtra.getBalance() / 100));
        responseDTO.setServiceFeeRate(redEnvelopeConfig.getServiceFeeRate().toString());
        return responseDTO;
    }

    /**
     * 发布红包
     */
    @Transactional
    public SendResponseDTO send(Integer uid, Integer templateId, String commandText, Integer fee, Integer number) {
        if (!NumberUtils.isValidId(uid) || !NumberUtils.isValidId(templateId) || StringUtils.isEmpty(commandText) || !NumberUtils.isValidId(fee) || !NumberUtils.isValidId(number)) {
            throw new CodeException(ErrorCode.PARAMS_ERROR);
        }
        // 红包不能少于1元，大于2000
        if (fee < 1 || fee > 2000) {
            throw new CodeException(ErrorCode.PARAMS_ERROR.getState(), "红包不能小于1元");
        }
        if (fee > 2000) {
            throw new CodeException(ErrorCode.PARAMS_ERROR.getState(), "红包大于2000元");
        }

        WechatUser wechatUser = wechatUserService.getUser(uid);
        if (wechatUser == null) {
            throw new CodeException(ErrorCode.PARAMS_ERROR.getState(), "用户不存在");
        }

        // 生成红包
        fee *= 100;        // 红包单位转换为分
        RedEnvelope redEnvelope = new RedEnvelope();
        redEnvelope.setUid(uid);
        redEnvelope.setTemplateId(templateId);
        redEnvelope.setCommandText(commandText);
        redEnvelope.setNumber(number);
        redEnvelope.setFee(fee);
        redEnvelope.setServiceFee(Double.valueOf(fee * redEnvelopeConfig.getServiceFeeRate()).intValue());
        redEnvelope.setIsFinish((byte)0);      // 是否结束。0-未结束；1-已结束。
        redEnvelope.setStatus((byte)0);     // 支付状态。0-未支付；1-支付成功；2-支付失败
        redEnvelope.setCreateTime(System.currentTimeMillis());
        redEnvelope.setModifyTime(System.currentTimeMillis());
        redEnvelopeMapper.insertSelective(redEnvelope);

        // 生成支付流水
        PayLog payLog = new PayLog();
        payLog.setUid(uid);
        payLog.setRedEnvelopeId(redEnvelope.getId());
        payLog.setTradeNo(OrderManager.getInstance().createOrderNo());
        payLog.setDescription("模板紅包-" + commandText);
        payLog.setFee(redEnvelope.getFee() + redEnvelope.getServiceFee());
        payLog.setType((byte) 1);   // 支付方式。0-余额；1-微信；2-支付宝
        payLog.setStatus((byte) 0);     // 支付状态。0-未支付；1-支付成功；2-支付失败

        // 提交微信预生成支付订单
        Map<String, String> payMap = wechatApi.prePay(wechatUser.getOpenId(), payLog.getTradeNo(), payLog.getDescription(), payLog.getFee().toString());
        if (payMap == null) {
            throw new CodeException(ErrorCode.PARAMS_ERROR.getState(), "请重新操作");
        }
        payLog.setOutTradeNo(payMap.get("prePayId"));
        payLog.setCreateTime(System.currentTimeMillis());
        payLogMapper.insertSelective(payLog);

        SendResponseDTO responseDTO = new SendResponseDTO();
        responseDTO.setRedEnvelopeId(redEnvelope.getId());
        responseDTO.setStatus(payLog.getStatus().intValue());
        responseDTO.setNonceStr(payMap.get("nonceStr"));
        responseDTO.setPackagea(payMap.get("package"));
        responseDTO.setTimeStamp(payMap.get("timeStamp"));
        responseDTO.setPaySign(payMap.get("paySign"));
        responseDTO.setSignType(payMap.get("signType"));
        return responseDTO;
    }

    /**
     * 调用此方法领取红包
     *
     * @param uid 用户id
     * @param redEnvelopeId 红包id
     * @return 返回领取红包对象
     */
    @Transactional
    @RequestLockable(key = "redEnvelopeId")
    public RedEnvelopeReceive receiveRedEnvelope(Integer redEnvelopeId, Integer uid) {
        RedEnvelope redEnvelope = redEnvelopeMapper.selectByPrimaryKey(redEnvelopeId);
        if (redEnvelope == null) {
            throw new CodeException(ErrorCode.PARAMS_ERROR.getState(), "红包不存在");
        }
        if (redEnvelope.getStatus() != 1) {      // 支付状态。0-未支付；1-支付成功；2-支付失败
            throw new CodeException(ErrorCode.PARAMS_ERROR.getState(), "红包不存在");
        }
        if (redEnvelope.getIsFinish() == 1) {     // 是否结束。0-未结束；1-已结束。
            throw new CodeException(ErrorCode.PARAMS_ERROR.getState(), "红包已过期");
        }
        if (redEnvelope.getReceiveNumber() <= redEnvelope.getNumber()) {
            throw new CodeException(ErrorCode.PARAMS_ERROR.getState(), "红包已领完");
        }

        // 红包已领过，直接返回红包信息
        List<RedEnvelopeReceive> receives = redEnvelopeReceiveMapper.selectByUidOrReid(uid, redEnvelopeId, 0, 1);
        if (receives != null && receives.size() > 0) {
            return receives.get(0);

        }

        int money = redEnvelope.getFee() - redEnvelope.getReceiveFee();
        int count = redEnvelope.getNumber() - redEnvelope.getReceiveNumber();
        int min = ((int) Math.rint(Math.random() * (money / count)));
        min = min == 0 ? 1 : min;
        int receiveFee = RedEnvelopesUtil.randomRedPacket(money, min, 5 * min, count);
        // 保存领取记录
        RedEnvelopeReceive redEnvelopeReceive = new RedEnvelopeReceive();
        redEnvelopeReceive.setUid(uid);
        redEnvelopeReceive.setRedEnvelopeId(redEnvelopeId);
        redEnvelopeReceive.setFee(receiveFee);
        redEnvelopeReceive.setCreateTime(System.currentTimeMillis());
        redEnvelopeReceiveMapper.insertSelective(redEnvelopeReceive);

        RedEnvelope updateRedEnvelope = new RedEnvelope();
        updateRedEnvelope.setId(redEnvelope.getId());
        updateRedEnvelope.setReceiveNumber(redEnvelope.getReceiveNumber() + 1);
        updateRedEnvelope.setReceiveFee(redEnvelope.getFee() + receiveFee);
        updateRedEnvelope.setIsFinish((byte)(redEnvelope.getNumber().equals(redEnvelope.getReceiveNumber() + 1) ? 1 : 0));
        updateRedEnvelope.setModifyTime(System.currentTimeMillis());
        redEnvelopeMapper.updateByPrimaryKeySelective(updateRedEnvelope);

        //更新用户余额
        wechatUserService.updateUserBalance(uid, receiveFee);

        return redEnvelopeReceive;
    }

    /**
     * 余额提现
     */
    @Transactional
    @RequestLockable(key = "uid")
    public WithdrawalsResponseDTO withdrawals(Integer uid, Double fee) {
        UserExtra userExtra = wechatUserService.getUserExtra(uid);
        if (userExtra == null) {
            throw new CodeException(ErrorCode.PARAMS_ERROR.getState(), "用户不存在");
        }

        BigDecimal totalFee = new BigDecimal(fee);
        int withdrawalsFee = totalFee.multiply(new BigDecimal(100)).intValue();
        if (userExtra.getBalance() < fee) {
            throw new CodeException(ErrorCode.ERROR.getState(), "账号余额不足");
        }

        Withdrawals withdrawals = new Withdrawals();
        withdrawals.setUid(uid);
        withdrawals.setBalance(userExtra.getBalance() - withdrawalsFee);
        withdrawals.setFee(withdrawalsFee);
        withdrawals.setTradeNo(OrderManager.getInstance().createOrderNo());
        withdrawals.setDescription("余额提现");
        withdrawals.setStatus((byte) 0);            // 提现状态。0-等待处理；1-成功；2-失败
        withdrawals.setCreateTime(System.currentTimeMillis());
        withdrawals.setModifyTime(System.currentTimeMillis());
        withdrawalsMapper.insertSelective(withdrawals);

        //更新用户余额
        wechatUserService.updateUserBalance(uid, - withdrawalsFee);
        WithdrawalsResponseDTO responseDTO = new WithdrawalsResponseDTO();
        responseDTO.setBalance(String.format("%.2f", (double)withdrawals.getBalance() / 100));
        return responseDTO;
    }

    /**
     * 我发出的
     */
    public MyRedEnvelopeResponseDTO mySend(Integer uid, Integer offset, Integer limit) {
        MyRedEnvelopeResponseDTO responseDTO = new MyRedEnvelopeResponseDTO();
        MyRedEnvelope myRedEnvelope = redEnvelopeMapper.countByUid(uid);
        responseDTO.setNumber(myRedEnvelope.getMyNumber().toString());
        responseDTO.setFee(String.format("%.2f", (double) myRedEnvelope.getMyFee() / 100));

        List<MyRedEnvelope> redEnvelopes = redEnvelopeMapper.listByUid(uid, offset, limit);
        if (redEnvelopes != null && redEnvelopes.size() > 0) {
            WechatUser wechatUser = wechatUserService.getUser(uid);
            redEnvelopes.forEach(myRedEnvelope1 -> {
                myRedEnvelope1.setFee(myRedEnvelope1.getFee()/100);
                myRedEnvelope1.setRedEnvelopeUser(wechatUser);
            });
        }
        responseDTO.setRedEnvelopes(redEnvelopes);
        return responseDTO;
    }

    /**
     * 我接收的
     */
    public MyRedEnvelopeResponseDTO myReceive(Integer uid, Integer offset, Integer limit) {
        MyRedEnvelopeResponseDTO responseDTO = new MyRedEnvelopeResponseDTO();
        MyRedEnvelope myRedEnvelope = redEnvelopeReceiveMapper.countByUid(uid);
        responseDTO.setNumber(myRedEnvelope.getMyNumber().toString());
        if (myRedEnvelope.getMyFee() == null) {
            myRedEnvelope.setMyFee(0);
        }
        responseDTO.setFee(String.format("%.2f", (double) myRedEnvelope.getMyFee() / 100));

        List<MyRedEnvelope> redEnvelopes = redEnvelopeReceiveMapper.listByUid(uid, offset, limit);
        if (redEnvelopes != null && redEnvelopes.size() > 0) {
            redEnvelopes.forEach(myRedEnvelope1 -> {
                myRedEnvelope1.setFee(myRedEnvelope1.getFee()/100);
                myRedEnvelope1.setRedEnvelopeUser(wechatUserService.getUser(myRedEnvelope1.getUid()));
            });
        }
        responseDTO.setRedEnvelopes(redEnvelopes);
        return responseDTO;
    }

    /**
     * 支付结果通知
     */
    @Transactional
    public void payNotify(Map<String, String> notifyMap) {
        // 校验订单是否存在和处理过
        // 校验返回金额是否正确
        String trade_no = notifyMap.get("out_trade_no");
        String totalFee = notifyMap.get("total_fee");
        PayLog payLog = payLogMapper.selectByTradeNo(trade_no);
        if (payLog == null) {
            throw new CodeException(ErrorCode.PARAMS_ERROR.getState(), "订单号不存在");
        }

        if (payLog.getStatus() == 0 && !StringUtils.isEmpty(totalFee) && payLog.getFee().intValue() == Integer.getInteger(totalFee).intValue()) {
            String zfjg = notifyMap.get("result_code");
            String zfsj = notifyMap.get("time_end");
            PayLog updatePayLog = new PayLog();
            updatePayLog.setId(payLog.getId());
            updatePayLog.setStatus((byte)("SUCCESS".equals(zfjg) ? 1 : 2));     // 支付状态。0-未支付；1-支付成功；2-支付失败
            updatePayLog.setPayTime(DateUtil.getDateFromString(zfsj, "yyyyMMddHHmmss").getTime());
            updatePayLog.setNote("err_code: " + notifyMap.get("err_code") + ", err_code_des: " + notifyMap.get("err_code_des"));
            payLogMapper.updateByPrimaryKeySelective(updatePayLog);

            RedEnvelope redEnvelope = redEnvelopeMapper.selectByPrimaryKey(payLog.getRedEnvelopeId());
            if (redEnvelope != null) {
                RedEnvelope updateRedEnvelope = new RedEnvelope();
                updateRedEnvelope.setId(redEnvelope.getId());
                updateRedEnvelope.setStatus(updatePayLog.getStatus());
                updateRedEnvelope.setModifyTime(System.currentTimeMillis());
                redEnvelopeMapper.updateByPrimaryKeySelective(updateRedEnvelope);
            }
        }

    }

}
