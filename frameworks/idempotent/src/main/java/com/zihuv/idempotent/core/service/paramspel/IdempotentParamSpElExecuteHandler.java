package com.zihuv.idempotent.core.service.paramspel;

import com.zihuv.idempotent.core.AbstractIdempotentExecuteHandler;
import com.zihuv.idempotent.pojo.IdempotentParamWrapper;
import org.aspectj.lang.ProceedingJoinPoint;

public class IdempotentParamSpElExecuteHandler extends AbstractIdempotentExecuteHandler implements IdempotentParamSpELService{

    @Override
    protected String generateLockKey(ProceedingJoinPoint joinPoint) {
        return null;
    }

    @Override
    public void handler(IdempotentParamWrapper idempotentParamWrapper) {

    }
}