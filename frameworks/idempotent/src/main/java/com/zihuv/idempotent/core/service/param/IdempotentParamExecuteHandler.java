package com.zihuv.idempotent.core.service.param;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.zihuv.convention.exception.ClientException;
import com.zihuv.idempotent.annotation.Idempotent;
import com.zihuv.idempotent.core.AbstractIdempotentExecuteHandler;
import com.zihuv.idempotent.core.IdempotentExecuteHandler;
import com.zihuv.idempotent.pojo.IdempotentParamWrapper;
import com.zihuv.index12306.frameworks.starter.user.core.UserContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
public class IdempotentParamExecuteHandler extends AbstractIdempotentExecuteHandler implements IdempotentExecuteHandler {

    private final RedisTemplate<String,Object> redisTemplate;

    @Override
    protected String buildLockKey(ProceedingJoinPoint joinPoint, Idempotent idempotent) {
        // key = 接口 + 用户 id + md5 请求参数
        return StrUtil.format("idempotent:path:{}:{}:userId:{}:md5:{}", getServletPath(), idempotent.uniqueKeyPrefix(),getCurrentUserId(), calcArgsMD5(joinPoint));
    }

    @Override
    public void handler(IdempotentParamWrapper idempotentParam) {
        String lockKey = idempotentParam.getLockKey();

        Boolean isNotExist = redisTemplate.opsForValue().setIfAbsent(lockKey, "", idempotentParam.getIdempotent().keyTimeout(), TimeUnit.SECONDS);
        if (!Boolean.TRUE.equals(isNotExist)) {
            log.info("[幂等模块][PARAM] key：<{}> 被多次投递，已将该消息丢弃", idempotentParam.getLockKey());
            throw new ClientException(idempotentParam.getIdempotent().message());
        }
    }

    /**
     * @return 获取当前线程上下文 ServletPath
     */
    private String getServletPath() {
        ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return sra != null ? sra.getRequest().getServletPath() : null;
    }

    /**
     * @return 当前操作用户 ID
     */
    private Long getCurrentUserId() {
        return UserContext.getUserId();
    }

    /**
     * @return md5 请求参数
     */
    private String calcArgsMD5(ProceedingJoinPoint joinPoint) {
        return DigestUtil.md5Hex(Arrays.toString(joinPoint.getArgs()));
    }
}