package com.zzhl.config;

import com.github.wxpay.sdk.WXPayConfig;
import org.apache.tomcat.util.bcel.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component
public class PayConfig implements WXPayConfig {

    @Autowired
    private WechatConfig wechatConfig;

    @Override
    public String getAppID() {
        return wechatConfig.getMiniProgram().getAppId();
    }

    @Override
    public String getMchID() {
        return wechatConfig.getMerchantPlatform().getMchId();
    }

    @Override
    public String getKey() {
        return wechatConfig.getMerchantPlatform().getApiKey();
    }

    @Override
    public InputStream getCertStream() {
        return null;
    }

    @Override
    public int getHttpConnectTimeoutMs() {
        return 6 * 1000;
    }

    @Override
    public int getHttpReadTimeoutMs() {
        return 8 * 1000;
    }
}
