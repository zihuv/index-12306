package com.zihuv.payservice.config;

import com.alipay.api.*;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class AliPayConfiguration {

    private final AliPayProperties aliPayProperties;

    @Bean
    public AlipayClient alipayClient() throws AlipayApiException {
        AlipayConfig alipayConfig = new AlipayConfig();
        // 设置网关地址
        alipayConfig.setServerUrl(aliPayProperties.getServerUrl());
        // 设置应用ID
        alipayConfig.setAppId(aliPayProperties.getAppId());
        // 设置应用私钥
        alipayConfig.setPrivateKey(aliPayProperties.getPrivateKey());
        // 设置请求格式，固定值 json
        alipayConfig.setFormat(AlipayConstants.FORMAT_JSON);
        // 设置字符集
        alipayConfig.setCharset(AlipayConstants.CHARSET_UTF8);
        // 设置签名类型
        alipayConfig.setSignType(AlipayConstants.SIGN_TYPE_RSA2);
        // 设置支付宝公钥
        alipayConfig.setAlipayPublicKey(aliPayProperties.getAlipayPublicKey());
        // 实例化客户端
        return new DefaultAlipayClient(alipayConfig);
    }
}