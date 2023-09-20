package com.zihuv.index12306.frameworks.starter.user.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zihuv.index12306.frameworks.starter.user.core.IUserContext;
import com.zihuv.index12306.frameworks.starter.user.core.impl.UserContextSaTokenImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserAutoConfiguration {

    @Bean
    public IUserContext iUserContext(ObjectMapper objectMapper) {
        return new UserContextSaTokenImpl(objectMapper);
    }

}