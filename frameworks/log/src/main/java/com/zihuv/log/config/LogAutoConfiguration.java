package com.zihuv.log.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zihuv.log.annotation.ILog;
import com.zihuv.log.core.ILogPrintAspect;
import com.zihuv.log.core.ILogService;
import com.zihuv.log.core.ILogServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LogAutoConfiguration {

    /**
     * {@link ILog} 日志打印 AOP 切面
     */
    @Bean
    public ILogPrintAspect iLogPrintAspect(HttpServletRequest request, ILogService logService,ObjectMapper objectMapper) {
        return new ILogPrintAspect(request,logService,objectMapper);
    }

    @Bean
    public ILogService iLogService() {
        return new ILogServiceImpl();
    }

    @Bean
    @ConditionalOnMissingBean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}