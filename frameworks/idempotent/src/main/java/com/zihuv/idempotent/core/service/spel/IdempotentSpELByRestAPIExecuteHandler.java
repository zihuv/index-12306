package com.zihuv.idempotent.core.service.spel;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.zihuv.convention.exception.ClientException;
import com.zihuv.idempotent.annotation.Idempotent;
import com.zihuv.idempotent.core.AbstractIdempotentExecuteHandler;
import com.zihuv.idempotent.pojo.IdempotentParamWrapper;
import com.zihuv.idempotent.utils.SpELUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
public class IdempotentSpELByRestAPIExecuteHandler extends AbstractIdempotentExecuteHandler implements IdempotentSpELService {

    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    protected String buildLockKey(ProceedingJoinPoint joinPoint, Idempotent idempotent) {
        // key = keyPrefix + spEL + md5 请求参数
        String keyPrefix = idempotent.uniqueKeyPrefix();
        String digestArgsByMd5 = DigestUtil.md5Hex(Arrays.toString(joinPoint.getArgs()));
        String spELKey = (String) SpELUtil.parseKey(idempotent.key(), ((MethodSignature) joinPoint.getSignature()).getMethod(), joinPoint.getArgs());
        return StrUtil.format("idempotent:spEL:{}:{}:md5:{}", spELKey, keyPrefix, digestArgsByMd5);
    }

    @Override
    public void handler(IdempotentParamWrapper idempotentParam) {
        String lockKey = idempotentParam.getLockKey();
        Boolean isNotExist = redisTemplate.opsForValue().setIfAbsent(lockKey, "", idempotentParam.getIdempotent().keyTimeout(), TimeUnit.SECONDS);

        if (!Boolean.TRUE.equals(isNotExist)) {
            log.info("[幂等模块][SPEL] key：<{}> 被多次投递，已将该消息丢弃", idempotentParam.getLockKey());
            throw new ClientException(idempotentParam.getIdempotent().message());
        }
    }
}