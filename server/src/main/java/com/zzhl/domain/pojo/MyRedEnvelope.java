package com.zzhl.domain.pojo;

public class MyRedEnvelope extends RedEnvelope {
    private Integer myNumber;
    private Integer myFee;

    private WechatUser redEnvelopeUser;     // 红包所有人

    public Integer getMyNumber() {
        return myNumber;
    }

    public void setMyNumber(Integer myNumber) {
        this.myNumber = myNumber;
    }

    public Integer getMyFee() {
        return myFee;
    }

    public void setMyFee(Integer myFee) {
        this.myFee = myFee;
    }

    public WechatUser getRedEnvelopeUser() {
        return redEnvelopeUser;
    }

    public void setRedEnvelopeUser(WechatUser redEnvelopeUser) {
        this.redEnvelopeUser = redEnvelopeUser;
    }
}
