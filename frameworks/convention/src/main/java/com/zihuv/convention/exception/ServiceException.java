package com.zihuv.convention.exception;

import com.zihuv.convention.errcode.BaseErrorCode;

import java.util.Optional;


/**
 * 服务端异常
 */
public class ServiceException extends AbstractException {

    public ServiceException(String message) {
        this(message, null, BaseErrorCode.SERVICE_ERROR);
    }

    public ServiceException(BaseErrorCode errorCode) {
        this(null, errorCode);
    }

    public ServiceException(String message, BaseErrorCode errorCode) {
        this(message, null, errorCode);
    }

    public ServiceException(String message, Throwable throwable, BaseErrorCode errorCode) {
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
