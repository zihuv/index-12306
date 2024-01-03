package com.zihuv.limiter.config;

import com.zihuv.limiter.core.LimiterAspect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LimiterAutoConfiguration {

    @Bean
    public LimiterAspect limiterAspect() {
        return new LimiterAspect();
    }
}