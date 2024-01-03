package com.zihuv.limiter.core;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.util.concurrent.RateLimiter;
import com.zihuv.convention.exception.ClientException;
import com.zihuv.limiter.annotation.Limiter;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

@Slf4j
@Aspect
public class LimiterAspect {

    static Cache<String, RateLimiter> limiterCache = CacheBuilder.newBuilder().build();

    @Around("@annotation(limiter)")
    public Object idempotentHandler(ProceedingJoinPoint joinPoint, Limiter limiter) throws Throwable {
        RateLimiter rateLimiter = limiterCache.get(limiter.key(), () -> RateLimiter.create(limiter.permitsPerSecond()));
        boolean success = rateLimiter.tryAcquire();
        if (!success) {
            log.warn("[限流系统] 限流 key：{} 开始限流", limiter.key());
            throw new ClientException(limiter.msg());
        }
        return joinPoint.proceed();
    }
}