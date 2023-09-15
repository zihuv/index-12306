package com.zihuv.log.core;

import cn.hutool.core.date.LocalDateTimeUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zihuv.log.annotation.ILog;
import com.zihuv.log.pojo.ILogDTO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Value;

@Aspect
@Slf4j
@RequiredArgsConstructor
public class ILogPrintAspect {

    private final HttpServletRequest request;
    private final ILogService logService;
    private final ObjectMapper objectMapper;

    @Value("${index-12306.insert-logs:true}")
    private boolean insertLogs;

    /**
     * 打印类或方法上的 {@link ILog}
     */
    @Around("@annotation(iLog)")
    public Object log(ProceedingJoinPoint joinPoint, ILog iLog) throws Throwable {
        ILogDTO logDTO = new ILogDTO();

        long startTime = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long endTime = System.currentTimeMillis();

        logDTO.setStartTime(LocalDateTimeUtil.of(startTime));
        logDTO.setSpendTime((int) (endTime - startTime));
        logDTO.setIp(request.getRemoteAddr());
        logDTO.setUri(request.getRequestURI());
        logDTO.setMethod(request.getMethod());

        if (insertLogs) {
            logService.logAsync(joinPoint, iLog, logDTO, result);
        }

        log.info("[{}] {}, executeTime: {}ms, info: {}", request.getMethod(), logDTO.getUri(),  logDTO.getSpendTime(), objectMapper.writeValueAsString(result));
        return result;
    }


}