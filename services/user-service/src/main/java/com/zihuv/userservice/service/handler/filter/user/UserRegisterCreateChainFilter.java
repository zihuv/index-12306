package com.zihuv.userservice.service.handler.filter.user;

import com.zihuv.designpattern.chain.AbstractChainHandler;
import com.zihuv.userservice.model.param.UserRegisterParam;

/**
 * 用户注册责任链过滤器
 */
public interface UserRegisterCreateChainFilter<T extends UserRegisterParam> extends AbstractChainHandler<UserRegisterParam> {

    @Override
    default String mark() {
        return "USER_REGISTER_FILTER";
    }
}