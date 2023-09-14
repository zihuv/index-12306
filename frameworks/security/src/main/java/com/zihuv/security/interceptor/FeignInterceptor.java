package com.zihuv.security.interceptor;

import cn.dev33.satoken.same.SaSameUtil;
import feign.RequestInterceptor;
import feign.RequestTemplate;

/**
 * feign拦截器, 在feign请求发出之前，加入一些操作
 */
public class FeignInterceptor implements RequestInterceptor {

    // 为 Feign 的 RCP调用 添加请求头Same-Token
    @Override
    public void apply(RequestTemplate requestTemplate) {
        requestTemplate.header(SaSameUtil.SAME_TOKEN, SaSameUtil.getToken());

        // 如果希望被调用方有会话状态，此处就还需要将 satoken 添加到请求头中
        // requestTemplate.header(StpUtil.getTokenName(), StpUtil.getTokenValue());
    }
}