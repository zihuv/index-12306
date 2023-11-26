package com.zihuv.payservice.service.strategy.pay;

import com.zihuv.base.context.ApplicationContextHolder;
import com.zihuv.payservice.common.enums.PayStrategyEnum;
import com.zihuv.payservice.service.PayService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 策略模式上下文
 */
@Component
public class PayStrategyContext implements CommandLineRunner {

    private static Map<String, PayService> payServiceMap;

    public PayService getPayService(String payCode) {
        String className = PayStrategyEnum.getClassNameByPayCode(payCode);
        return payServiceMap.get(className);
    }

    @Override
    public void run(String... args)  {
        payServiceMap = ApplicationContextHolder.getBeansOfType(PayService.class);
    }
}