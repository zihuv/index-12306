package com.zihuv.security.config;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.filter.SaServletFilter;
import cn.dev33.satoken.same.SaSameUtil;
import cn.dev33.satoken.util.SaResult;
import com.zihuv.security.interceptor.FeignInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SecurityAutoConfiguration {

    @Value("${index-12306.check-same-token:true}")
    private boolean requireSameToken;

    // 注册 Sa-Token 全局过滤器
    @Bean
    public SaServletFilter getSaServletFilter() {
        return new SaServletFilter()
                .addInclude("/**")
                .addExclude("/favicon.ico")
                .setAuth(obj -> {
                    // 校验 Same-Token 身份凭证
                    if (requireSameToken) {
                        String token = SaHolder.getRequest().getHeader(SaSameUtil.SAME_TOKEN);
                        SaSameUtil.checkToken(token);
                    }
                })
                .setError(e -> {
                    return SaResult.error(e.getMessage());
                })
                ;
    }

    @Bean
    public FeignInterceptor feignInterceptor() {
        return new FeignInterceptor();
    }
}