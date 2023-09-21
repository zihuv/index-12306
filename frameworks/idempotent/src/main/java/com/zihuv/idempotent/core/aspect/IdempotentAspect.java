package com.zihuv.idempotent.core.aspect;

import com.zihuv.idempotent.annotation.Idempotent;
import com.zihuv.idempotent.core.IdempotentExecuteHandler;
import com.zihuv.idempotent.core.factory.IdempotentExecuteHandlerFactory;
import lombok.SneakyThrows;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;

@Aspect
public class IdempotentAspect {

    @Around("@annotation(idempotent)")
    public Object idempotentHandler(ProceedingJoinPoint joinPoint, Idempotent idempotent) throws Throwable {
        IdempotentExecuteHandler instance = IdempotentExecuteHandlerFactory.getInstance(idempotent.scene(), idempotent.type());

        // TODO 对 MQ 消息的幂等性处理
        Object resultObj;
        try {
            instance.execute(joinPoint, idempotent);
            resultObj = joinPoint.proceed();
            instance.postProcessing();
        } catch (Exception e) {
            instance.exceptionProcessing();
            throw e;
        }
        return resultObj;
    }

    @SneakyThrows
    public static Idempotent getIdempotent(ProceedingJoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method targetMethod = joinPoint.getTarget().getClass().getDeclaredMethod(methodSignature.getName(), methodSignature.getMethod().getParameterTypes());
        return targetMethod.getAnnotation(Idempotent.class);
    }
}