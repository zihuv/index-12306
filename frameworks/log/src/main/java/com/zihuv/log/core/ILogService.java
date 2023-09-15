package com.zihuv.log.core;

import com.zihuv.log.annotation.ILog;
import com.zihuv.log.pojo.ILogDTO;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.scheduling.annotation.Async;

public interface ILogService {

    @Async
    void logAsync(ProceedingJoinPoint joinPoint, ILog log, ILogDTO logDTO, Object result);
}