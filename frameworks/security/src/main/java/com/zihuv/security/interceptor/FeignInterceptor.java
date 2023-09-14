package com.zihuv.security.interceptor;

import cn.dev33.satoken.same.SaSameUtil;
import feign.RequestInterceptor;
import feign.RequestTemplate;

/**
 * Feign 拦截器, 在 Feign 请求发出之前，加入一些操作
 */
public class FeignInterceptor implements RequestInterceptor {

    // 为 Feign 的 RCP调用 添加请求头Same-Token
    @Override
    public void apply(RequestTemplate requestTemplate) {
        requestTemplate.header(SaSameUtil.SAME_TOKEN, SaSameUtil.getToken());
    }
}