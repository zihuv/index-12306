package com.zihuv.idempotent.core;

import com.zihuv.idempotent.annotation.Idempotent;
import com.zihuv.idempotent.pojo.IdempotentParamWrapper;
import org.aspectj.lang.ProceedingJoinPoint;

public abstract class AbstractIdempotentExecuteHandler implements IdempotentExecuteHandler {

    /**
     * 生成锁的 key 的名称
     *
     * @param joinPoint  AOP 方法处理
     * @param idempotent 幂等性注解
     * @return 幂等参数包装器
     */
    protected abstract String generateLockKey(ProceedingJoinPoint joinPoint, Idempotent idempotent);

    /**
     * 执行幂等处理逻辑
     *
     * @param joinPoint  AOP 方法处理
     * @param idempotent 幂等注解
     */
    public void execute(ProceedingJoinPoint joinPoint, Idempotent idempotent) {
        // 模板方法模式：构建幂等参数对象
        String lockKey = generateLockKey(joinPoint, idempotent);

        IdempotentParamWrapper idempotentParamWrapper = new IdempotentParamWrapper();
        idempotentParamWrapper.setJoinPoint(joinPoint);
        idempotentParamWrapper.setIdempotent(idempotent);
        idempotentParamWrapper.setLockKey(lockKey);

        handler(idempotentParamWrapper);
    }
}