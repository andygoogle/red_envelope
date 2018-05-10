package com.zzhl.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "wechat")
public class WechatConfig {
    private MiniProgram miniProgram;
    private MerchantPlatform merchantPlatform;
    private OpenPlatform openPlatform;
    private Pay pay;

    public MiniProgram getMiniProgram() {
        return miniProgram;
    }

    public void setMiniProgram(MiniProgram miniProgram) {
        this.miniProgram = miniProgram;
    }

    public MerchantPlatform getMerchantPlatform() {
        return merchantPlatform;
    }

    public void setMerchantPlatform(MerchantPlatform merchantPlatform) {
        this.merchantPlatform = merchantPlatform;
    }

    public OpenPlatform getOpenPlatform() {
        return openPlatform;
    }

    public void setOpenPlatform(OpenPlatform openPlatform) {
        this.openPlatform = openPlatform;
    }

    public Pay getPay() {
        return pay;
    }

    public void setPay(Pay pay) {
        this.pay = pay;
    }

    public static class MiniProgram {
        private String appId;
        private String secret;

        public String getAppId() {
            return appId;
        }

        public void setAppId(String appId) {
            this.appId = appId;
        }

        public String getSecret() {
            return secret;
        }

        public void setSecret(String secret) {
            this.secret = secret;
        }

        @Override
        public String toString() {
            return "MiniProgram{" +
                    "appId='" + appId + '\'' +
                    ", secret='" + secret + '\'' +
                    '}';
        }
    }

    public static class MerchantPlatform {
        private String mchId;
        private String apiKey;

        public String getMchId() {
            return mchId;
        }

        public void setMchId(String mchId) {
            this.mchId = mchId;
        }

        public String getApiKey() {
            return apiKey;
        }

        public void setApiKey(String apiKey) {
            this.apiKey = apiKey;
        }

        @Override
        public String toString() {
            return "MerchantPlatform{" +
                    "mchId='" + mchId + '\'' +
                    ", apiKey='" + apiKey + '\'' +
                    '}';
        }
    }

    public static class OpenPlatform {
        private String appId;
        private String noticeToken;
        private String noticeEncodingAesKey;

        public String getAppId() {
            return appId;
        }

        public void setAppId(String appId) {
            this.appId = appId;
        }

        public String getNoticeToken() {
            return noticeToken;
        }

        public void setNoticeToken(String noticeToken) {
            this.noticeToken = noticeToken;
        }

        public String getNoticeEncodingAesKey() {
            return noticeEncodingAesKey;
        }

        public void setNoticeEncodingAesKey(String noticeEncodingAesKey) {
            this.noticeEncodingAesKey = noticeEncodingAesKey;
        }

        @Override
        public String toString() {
            return "OpenPlatform{" +
                    "appId='" + appId + '\'' +
                    ", noticeToken='" + noticeToken + '\'' +
                    ", noticeEncodingAesKey='" + noticeEncodingAesKey + '\'' +
                    '}';
        }
    }

    public static class Pay {
        private String notifyUrl;
        private String spbillCreateIp;
        private String tradeType;

        public String getNotifyUrl() {
            return notifyUrl;
        }

        public void setNotifyUrl(String notifyUrl) {
            this.notifyUrl = notifyUrl;
        }

        public String getSpbillCreateIp() {
            return spbillCreateIp;
        }

        public void setSpbillCreateIp(String spbillCreateIp) {
            this.spbillCreateIp = spbillCreateIp;
        }

        public String getTradeType() {
            return tradeType;
        }

        public void setTradeType(String tradeType) {
            this.tradeType = tradeType;
        }

        @Override
        public String toString() {
            return "Pay{" +
                    "notifyUrl='" + notifyUrl + '\'' +
                    ", spbillCreateIp='" + spbillCreateIp + '\'' +
                    ", tradeType='" + tradeType + '\'' +
                    '}';
        }
    }

}
