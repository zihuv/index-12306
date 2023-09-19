package com.zihuv.idempotent.core.aspect;

import com.zihuv.idempotent.annotation.Idempotent;
import com.zihuv.idempotent.core.IdempotentExecuteHandler;
import com.zihuv.idempotent.core.factory.IdempotentExecuteHandlerFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class IdempotentAspect {

    @Around("@annotation(idempotent)")
    public Object idempotentHandler(ProceedingJoinPoint joinPoint, Idempotent idempotent) throws Throwable {
        IdempotentExecuteHandler instance = IdempotentExecuteHandlerFactory.getInstance(idempotent.scene(), idempotent.type());
        instance.execute(joinPoint, idempotent);
        // TODO 对 MQ 消息的幂等性处理
        return joinPoint.proceed();
    }
}