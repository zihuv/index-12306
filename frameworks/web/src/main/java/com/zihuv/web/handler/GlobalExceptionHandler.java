package com.zihuv.web.handler;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.zihuv.convention.errcode.ErrorCode;
import com.zihuv.convention.exception.AbstractException;
import com.zihuv.convention.result.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Optional;

/**
 * 全局异常处理器
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 拦截参数验证异常
     */
    @SneakyThrows
    @ExceptionHandler(value = ConstraintViolationException.class)
    public Result<?> validExceptionHandler(HttpServletRequest request, ConstraintViolationException ex) {
        log.error("[{}] {} [ex] {}", request.getMethod(), getUrl(request), ex.getMessage());
        return Result.fail(ex.getMessage());
    }

    /**
     * 拦截应用内抛出的异常
     */
    @ExceptionHandler(value = {AbstractException.class})
    public Result<?> abstractException(HttpServletRequest request, AbstractException ex) {
        if (ex.getCause() != null) {
            log.error("[{}] {} [ex] {}", request.getMethod(), request.getRequestURL().toString(), ex.toString(), ex.getCause());
            return Result.fail(String.valueOf(ex));
        }
        log.error("[{}] {} [ex] {}", request.getMethod(), request.getRequestURL().toString(), ex.toString());
        return Result.fail(String.valueOf(ex));
    }

    /**
     * 拦截未捕获异常
     */
    @ExceptionHandler(value = Exception.class)
    public Result<?> defaultErrorHandler(HttpServletRequest request, Exception e) {
        log.error("[{}] {} ", request.getMethod(), getUrl(request), e);
        return Result.fail(String.valueOf(e.getMessage()) );
    }

    private String getUrl(HttpServletRequest request) {
        if (request.getQueryString() == null || "".equals(request.getQueryString())) {
            return request.getRequestURL().toString();
        }
        return request.getRequestURL().toString() + "?" + request.getQueryString();
    }
}
