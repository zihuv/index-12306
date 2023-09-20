package com.zihuv.index12306.frameworks.starter.user.core;

import com.zihuv.base.context.ApplicationContextHolder;
import lombok.RequiredArgsConstructor;

/**
 * 用户上下文工具类
 */
@RequiredArgsConstructor
public class UserContext {

    private static final IUserContext iUserContext = ApplicationContextHolder.getBean(IUserContext.class);

    public static String getUsername() {
        return iUserContext.getUsername();
    }

    public static Long getUserId() {
        return iUserContext.getUserId();
    }
}