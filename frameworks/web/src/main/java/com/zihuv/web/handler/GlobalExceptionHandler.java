package com.zihuv.web.handler;

import com.zihuv.convention.exception.AbstractException;
import com.zihuv.convention.result.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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
        log.error("[{}] {} [ex] {}", request.getMethod(), getUrl(request), ex.getErrorMessage());
        return Result.fail(ex.getErrorMessage());
    }

    /**
     * 拦截未捕获异常
     */
    @ExceptionHandler(value = Exception.class)
    public Result<?> defaultErrorHandler(HttpServletRequest request, Exception e) {
        // 可预料的异常
        if (e.getClass().getName().equals("cn.dev33.satoken.exception.NotLoginException")) {
            log.error("[{}] {} [ex] {}", request.getMethod(), getUrl(request), e.getMessage());
        } else if (e.getClass().getName().equals("org.springframework.web.bind.MethodArgumentNotValidException")) {
            log.error("[{}] {} [ex] {}", request.getMethod(), getUrl(request), e.getMessage());
        } else {
            // 未预料到的异常
            log.error("[{}] {} ", request.getMethod(), getUrl(request), e);
        }
        return Result.fail(e.getMessage());
    }

    private String getUrl(HttpServletRequest request) {
        if (request.getQueryString() == null || "".equals(request.getQueryString())) {
            return request.getRequestURL().toString();
        }
        return request.getRequestURL().toString() + "?" + request.getQueryString();
    }
}
