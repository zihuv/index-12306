package com.zihuv.idempotent.config;

import com.zihuv.idempotent.core.aspect.IdempotentAspect;
import com.zihuv.idempotent.core.service.param.IdempotentParamExecuteHandler;
import com.zihuv.idempotent.core.service.param.IdempotentParamService;
import com.zihuv.idempotent.core.service.spel.IdempotentSpELByMQExecuteHandler;
import com.zihuv.idempotent.core.service.spel.IdempotentSpELByRestAPIExecuteHandler;
import com.zihuv.idempotent.core.service.spel.IdempotentSpELService;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
public class IdempotentAutoConfiguration {

    /**
     * 幂等切面
     */
    @Bean
    public IdempotentAspect idempotentAspect() {
        return new IdempotentAspect();
    }

    /**
     * 参数方式幂等实现，基于 RestAPI 场景
     */
    @Bean
    public IdempotentParamService idempotentParamService(RedissonClient redissonClient, RedisTemplate<String, Object> redisTemplate) {
        return new IdempotentParamExecuteHandler(redissonClient, redisTemplate);
    }

    /**
     * SpEL 方式幂等实现，基于 RestAPI 场景
     */
    @Bean
    public IdempotentSpELService idempotentSpELByRestAPIExecuteHandler(RedissonClient redissonClient, RedisTemplate<String, Object> redisTemplate) {
        return new IdempotentSpELByRestAPIExecuteHandler(redissonClient, redisTemplate);
    }

    /**
     * SpEL 方式幂等实现，基于 MQ 场景
     */
    @Bean
    @ConditionalOnMissingBean
    public IdempotentSpELByMQExecuteHandler idempotentSpELByMQExecuteHandler() {
        return new IdempotentSpELByMQExecuteHandler();
    }
}