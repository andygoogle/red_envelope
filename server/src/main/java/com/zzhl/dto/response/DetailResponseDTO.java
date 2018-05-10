package com.zzhl.dto.response;

import com.zzhl.domain.pojo.MyRedEnvelope;
import com.zzhl.domain.pojo.RedEnvelope;
import com.zzhl.domain.pojo.WechatUser;

import java.util.List;

public class DetailResponseDTO {
    private String myFee;
    private Integer receiveStatus;
    private RedEnvelope redEnvelope;
    private WechatUser redEnvelopeUser;     // 红包所有人
    private List<MyRedEnvelope> redEnvelopes;

    public String getMyFee() {
        return myFee;
    }

    public void setMyFee(String myFee) {
        this.myFee = myFee;
    }

    public Integer getReceiveStatus() {
        return receiveStatus;
    }

    public void setReceiveStatus(Integer receiveStatus) {
        this.receiveStatus = receiveStatus;
    }

    public RedEnvelope getRedEnvelope() {
        return redEnvelope;
    }

    public void setRedEnvelope(RedEnvelope redEnvelope) {
        this.redEnvelope = redEnvelope;
    }

    public WechatUser getRedEnvelopeUser() {
        return redEnvelopeUser;
    }

    public void setRedEnvelopeUser(WechatUser redEnvelopeUser) {
        this.redEnvelopeUser = redEnvelopeUser;
    }

    public List<MyRedEnvelope> getRedEnvelopes() {
        return redEnvelopes;
    }

    public void setRedEnvelopes(List<MyRedEnvelope> redEnvelopes) {
        this.redEnvelopes = redEnvelopes;
    }
}
