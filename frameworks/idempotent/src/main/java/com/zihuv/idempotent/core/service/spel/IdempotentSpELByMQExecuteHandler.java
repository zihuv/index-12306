package com.zihuv.idempotent.core.service.spel;

import com.zihuv.idempotent.annotation.Idempotent;
import com.zihuv.idempotent.pojo.IdempotentParamWrapper;
import com.zihuv.idempotent.core.AbstractIdempotentExecuteHandler;
import org.aspectj.lang.ProceedingJoinPoint;

public class IdempotentSpELByMQExecuteHandler extends AbstractIdempotentExecuteHandler implements IdempotentSpELService{
    @Override
    protected String generateLockKey(ProceedingJoinPoint joinPoint, Idempotent idempotent) {
        return null;
    }

    @Override
    public void handler(IdempotentParamWrapper idempotentParam) {

    }
}