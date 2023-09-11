package com.zihuv.designpattern.config;

import com.zihuv.designpattern.chain.AbstractChainContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DesignPatternAutoConfiguration {
    /**
     * 责任链上下文
     */
    @Bean
    public AbstractChainContext<?> abstractChainContext() {
        return new AbstractChainContext<>();
    }
}