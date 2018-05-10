package com.zzhl.dto.requst;

public class WithdrawalsRequestDTO {
    private Integer uid;
    private Double fee;

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public Double getFee() {
        return fee;
    }

    public void setFee(Double fee) {
        this.fee = fee;
    }
}
