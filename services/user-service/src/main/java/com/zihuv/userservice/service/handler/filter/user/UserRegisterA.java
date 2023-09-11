package com.zihuv.userservice.service.handler.filter.user;

import com.zihuv.userservice.model.param.UserRegisterParam;
import org.springframework.stereotype.Component;

@Component
public class UserRegisterA implements UserRegisterCreateChainFilter<UserRegisterParam> {
    @Override
    public void handler(UserRegisterParam requestParam) {
        System.out.println("A");
    }

    @Override
    public int getOrder() {
        return 2;
    }
}