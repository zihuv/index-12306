package com.zihuv.idempotent.core.service.param;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.zihuv.convention.exception.ClientException;
import com.zihuv.idempotent.annotation.Idempotent;
import com.zihuv.idempotent.core.AbstractIdempotentExecuteHandler;
import com.zihuv.idempotent.core.IdempotentExecuteHandler;
import com.zihuv.idempotent.pojo.IdempotentParamWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
public class IdempotentParamByMQExecuteHandler extends AbstractIdempotentExecuteHandler implements IdempotentExecuteHandler {

    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    protected String buildLockKey(ProceedingJoinPoint joinPoint, Idempotent idempotent) {
        return StrUtil.format("idempotent:mq:{}:message:{}", idempotent.uniqueKeyPrefix(), calcArgsSHA256(joinPoint));
    }

    @Override
    public void handler(IdempotentParamWrapper idempotentParam) {
        String lockKey = idempotentParam.getLockKey();

        Boolean isNotExist = redisTemplate.opsForValue().setIfAbsent(lockKey, "", idempotentParam.getIdempotent().keyTimeout(), TimeUnit.SECONDS);
        if (!Boolean.TRUE.equals(isNotExist)) {
            log.info("[幂等模块][MQ] key：<{}> 被多次投递，已将该消息丢弃", idempotentParam.getLockKey());
            throw new ClientException(idempotentParam.getIdempotent().message());
        }
    }

    private String calcArgsSHA256(ProceedingJoinPoint joinPoint) {
        return DigestUtil.sha256Hex(Arrays.toString(joinPoint.getArgs()));
    }
}