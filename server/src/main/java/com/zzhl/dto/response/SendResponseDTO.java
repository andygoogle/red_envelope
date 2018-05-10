package com.zzhl.dto.response;

public class SendResponseDTO {
    private Integer redEnvelopeId;
    private Integer status;       // 支付状态。0-未支付；1-支付成功；2-支付失败
    private String timeStamp;
    private String nonceStr;
    private String packagea;
    private String signType;
    private String paySign;

    public Integer getRedEnvelopeId() {
        return redEnvelopeId;
    }

    public void setRedEnvelopeId(Integer redEnvelopeId) {
        this.redEnvelopeId = redEnvelopeId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getNonceStr() {
        return nonceStr;
    }

    public void setNonceStr(String nonceStr) {
        this.nonceStr = nonceStr;
    }

    public String getPackagea() {
        return packagea;
    }

    public void setPackagea(String packagea) {
        this.packagea = packagea;
    }

    public String getSignType() {
        return signType;
    }

    public void setSignType(String signType) {
        this.signType = signType;
    }

    public String getPaySign() {
        return paySign;
    }

    public void setPaySign(String paySign) {
        this.paySign = paySign;
    }
}
