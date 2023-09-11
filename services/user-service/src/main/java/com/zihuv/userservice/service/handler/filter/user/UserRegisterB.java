package com.zihuv.userservice.service.handler.filter.user;

import com.zihuv.userservice.model.param.UserRegisterParam;
import org.springframework.stereotype.Component;

@Component
public class UserRegisterB implements UserRegisterCreateChainFilter<UserRegisterParam>{
    @Override
    public void handler(UserRegisterParam requestParam) {
        System.out.println("B");
    }

    @Override
    public int getOrder() {
        return 1;
    }
}