package com.zihuv.payservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "pay.alipay")
public class AliPayProperties {

    /**
     * 开放平台上创建的应用的 ID
     */
    private String appId;

    /**
     * 商户私钥
     */
    private String privateKey;

    /**
     * 支付宝公钥字符串（公钥模式下设置，证书模式下无需设置）
     */
    private String alipayPublicKey;

    /**
     * 页面跳转同步通知页面
     */
    private String returnUrl;

    /**
     * 网关地址
     */
    private String serverUrl;

    /**
     * 支付结果回调地址
     */
    private String notifyUrl;
}