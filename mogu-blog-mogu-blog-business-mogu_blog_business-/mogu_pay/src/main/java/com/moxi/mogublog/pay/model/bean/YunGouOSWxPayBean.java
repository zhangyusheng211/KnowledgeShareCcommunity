package com.moxi.mogublog.pay.model.bean;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * YunGouOS微信支付
 *
 * @author 陌溪
 */
@Component
@ConfigurationProperties(prefix = "yungouoswxpay")
public class YunGouOSWxPayBean {
    private String key;
    private String mchId;
    private String domain;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getMchId() {
        return mchId;
    }

    public void setMchId(String mchId) {
        this.mchId = mchId;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }
}