package com.zihuv.convention.exception;

import com.zihuv.convention.errcode.ErrorCode;

import java.util.Optional;


/**
 * 服务端异常
 */
public class ServiceException extends AbstractException {

    public ServiceException(String message) {
        this(message, null, ErrorCode.SERVICE_ERROR);
    }

    public ServiceException(ErrorCode errorCode) {
        this(null, errorCode);
    }

    public ServiceException(String message, ErrorCode errorCode) {
        this(message, null, errorCode);
    }

    public ServiceException(String message, Throwable throwable, ErrorCode errorCode) {
        super(Optional.ofNullable(message).orElse(errorCode.getMessage()), throwable, errorCode);
    }

    @Override
    public String toString() {
        return "ServiceException{" +
                "code='" + errorCode + "'," +
                "message='" + errorMessage + "'" +
                '}';
    }
}
