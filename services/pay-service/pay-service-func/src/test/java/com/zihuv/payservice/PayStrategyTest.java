package com.zihuv.payservice;

import com.zihuv.payservice.service.strategy.pay.PayStrategyContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class PayStrategyTest {

    @Autowired
    private PayStrategyContext payStrategyContext;

    @Test
    public void test01() {

    }
}