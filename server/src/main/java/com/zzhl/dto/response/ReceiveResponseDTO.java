package com.zzhl.dto.response;

public class ReceiveResponseDTO {
    private Integer redEnvelopeId;
    private String fee;      // 领取余额

    public Integer getRedEnvelopeId() {
        return redEnvelopeId;
    }

    public void setRedEnvelopeId(Integer redEnvelopeId) {
        this.redEnvelopeId = redEnvelopeId;
    }

    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }
}
