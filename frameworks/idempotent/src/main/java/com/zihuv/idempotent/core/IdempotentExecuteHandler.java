package com.zihuv.idempotent.core;

import com.zihuv.idempotent.annotation.Idempotent;
import com.zihuv.idempotent.pojo.IdempotentParamWrapper;
import org.aspectj.lang.ProceedingJoinPoint;

/**
 * 幂等执行处理器
 */
public interface IdempotentExecuteHandler {

    /**
     * 执行幂等处理逻辑（外部调用，用于封装参数，交给 handler 方法处理）
     *
     * @param joinPoint  AOP 方法处理
     * @param idempotent 幂等注解
     */
    void execute(ProceedingJoinPoint joinPoint, Idempotent idempotent);

    /**
     * 幂等处理逻辑（内部处理方法）
     *
     * @param idempotentParam 幂等参数
     */
    void handler(IdempotentParamWrapper idempotentParam);

}
