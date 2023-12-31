package com.zihuv.gateway.config;

import cn.dev33.satoken.reactor.filter.SaReactorFilter;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.List;

@Configuration
public class SaTokenGatewayConfiguration {

    /**
     * 注册 Sa-Token 全局鉴权过滤器
     */
    @Bean
    public SaReactorFilter getSaReactorFilter() {
        return new SaReactorFilter()
                // 拦截地址
                .addInclude("/**")    /* 拦截全部path */
                // 开放地址
                .addExclude("/favicon.ico","/doc.html")
                // 鉴权方法：每次访问进入
                .setAuth(obj -> {
                    // 登录校验 -- 拦截所有路由，并排除/user/doLogin 用于开放登录
                   // SaRouter.match("/**", "/user/doLogin", r -> StpUtil.checkLogin());

                    // 权限认证 -- 不同模块, 校验不同权限
//                    SaRouter.match("/user/**", r -> StpUtil.checkPermission("user"));
//                    SaRouter.match("/admin/**", r -> StpUtil.checkPermission("admin"));
//                    SaRouter.match("/goods/**", r -> StpUtil.checkPermission("goods"));
//                    SaRouter.match("/orders/**", r -> StpUtil.checkPermission("orders"));
                })
                // 异常处理方法：每次setAuth函数出现异常时进入
                .setError(e -> {
                    return SaResult.error(e.getMessage());
                })
                ;
    }

    /**
     * 自定义权限验证接口扩展
     */
    @Component
    public static class StpInterfaceImpl implements StpInterface {
        // TODO 授权
        @Override
        public List<String> getPermissionList(Object loginId, String loginType) {
            // 返回此 loginId 拥有的权限列表
            return null;
        }

        @Override
        public List<String> getRoleList(Object loginId, String loginType) {
            // 返回此 loginId 拥有的角色列表
            return null;
        }

    }
}

