package com.zihuv.idempotent.core.service.spel;

import com.zihuv.idempotent.core.AbstractIdempotentExecuteHandler;
import com.zihuv.idempotent.pojo.IdempotentParamWrapper;
import org.aspectj.lang.ProceedingJoinPoint;

public class IdempotentSpELByRestAPIExecuteHandler extends AbstractIdempotentExecuteHandler implements IdempotentSpELService{
    @Override
    protected String generateLockKey(ProceedingJoinPoint joinPoint) {
        return null;
    }

    @Override
    public void handler(IdempotentParamWrapper idempotentParamWrapper) {

    }
}