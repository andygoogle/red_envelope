package com.zzhl.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "red-envelope")
public class RedEnvelopeConfig {
    private Double serviceFeeRate;
    private Integer enableScheduledTask;
    private String filePath;

    public Double getServiceFeeRate() {
        return serviceFeeRate;
    }

    public void setServiceFeeRate(Double serviceFeeRate) {
        this.serviceFeeRate = serviceFeeRate;
    }

    public Integer getEnableScheduledTask() {
        return enableScheduledTask;
    }

    public void setEnableScheduledTask(Integer enableScheduledTask) {
        this.enableScheduledTask = enableScheduledTask;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
