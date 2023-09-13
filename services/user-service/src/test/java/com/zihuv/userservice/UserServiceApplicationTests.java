package com.zihuv.userservice;

import com.zihuv.database.handler.CustomIdGenerator;
import com.zihuv.designpattern.chain.AbstractChainContext;
import com.zihuv.userservice.model.param.UserRegisterParam;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class UserServiceApplicationTests {
    @Autowired
    private AbstractChainContext<UserRegisterParam> abstractChainContext;

    @Autowired
    private CustomIdGenerator customIdGenerator;
    @Test
    void contextLoads() {
        customIdGenerator.nextId(null);
        customIdGenerator.nextId(null);
    }

}
