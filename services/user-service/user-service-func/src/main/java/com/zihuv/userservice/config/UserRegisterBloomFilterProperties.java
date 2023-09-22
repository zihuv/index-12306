package com.zihuv.userservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "framework.cache.redis.bloom-filter.user-register")
public class UserRegisterBloomFilterProperties {

    /**
     * 用户注册布隆过滤器实例名称
     */
    private String name = "bloom_filter:user_register_cache_penetration";

    /**
     * 每个元素的预期插入量
     */
    private Long expectedInsertions = 64000L;

    /**
     * 预期错误概率
     */
    private Double falseProbability = 0.03D;
}