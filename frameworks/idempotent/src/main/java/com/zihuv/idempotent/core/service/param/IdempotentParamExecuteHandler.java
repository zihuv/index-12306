package com.zihuv.idempotent.core.service.param;

import cn.hutool.crypto.digest.DigestUtil;
import com.zihuv.convention.exception.ClientException;
import com.zihuv.idempotent.core.AbstractIdempotentExecuteHandler;
import com.zihuv.idempotent.pojo.IdempotentParamWrapper;
import com.zihuv.index12306.frameworks.starter.user.core.UserContext;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;

@RequiredArgsConstructor
public class IdempotentParamExecuteHandler extends AbstractIdempotentExecuteHandler implements IdempotentParamService {

    private final
UserContext userContext;
    private final RedissonClient redissonClient;

    private final static String LOCK = "lock:param:restAPI";

    @Override
    protected String generateLockKey(ProceedingJoinPoint joinPoint) {
        // key = 接口 + 用户 id + md5 请求参数
        return String.format("idempotent:path:%s:currentUserId:%d:md5:%s", getServletPath(), getCurrentUserId(), calcArgsMD5(joinPoint));
    }

    @Override
    public void handler(IdempotentParamWrapper idempotentParam) {
        String lockKey = idempotentParam.getLockKey();
        RLock lock = redissonClient.getLock(lockKey);
        if (!lock.tryLock()) {
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
        return userContext.getUserId();
    }

    /**
     * @return md5 请求参数
     */
    private String calcArgsMD5(ProceedingJoinPoint joinPoint) {
        return DigestUtil.md5Hex(Arrays.toString(joinPoint.getArgs()));
    }
}