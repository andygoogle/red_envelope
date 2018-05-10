package com.zzhl.dto.response;

public class SendStep1ResponseDTO {
    private String balance;      // 用户余额
    private String serviceFeeRate;    // 服务费率

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getServiceFeeRate() {
        return serviceFeeRate;
    }

    public void setServiceFeeRate(String serviceFeeRate) {
        this.serviceFeeRate = serviceFeeRate;
    }
}
