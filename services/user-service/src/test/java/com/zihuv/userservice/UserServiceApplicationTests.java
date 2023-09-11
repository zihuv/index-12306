package com.zihuv.userservice;

import com.zihuv.designpattern.chain.AbstractChainContext;
import com.zihuv.userservice.model.param.UserRegisterParam;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class UserServiceApplicationTests {
    @Autowired
    private AbstractChainContext<UserRegisterParam> abstractChainContext;

    @Test
    void contextLoads() {
        abstractChainContext.handler("USER_REGISTER_FILTER",new UserRegisterParam());
    }

}
