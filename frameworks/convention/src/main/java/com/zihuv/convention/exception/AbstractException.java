package com.zihuv.convention.exception;

import com.zihuv.convention.errcode.IErrorCode;
import lombok.Getter;
import org.springframework.util.StringUtils;

import java.rmi.RemoteException;

/**
 * 抽象项目中三类异常体系，客户端异常、服务端异常以及远程服务调用异常
 *
 * @see ClientException
 * @see ServiceException
 * @see RemoteException
 */
@Getter
public abstract class AbstractException extends RuntimeException {

    public final String errorCode;

    public final String errorMessage;

    public AbstractException(String message, Throwable throwable, IErrorCode errorCode) {
        super(message, throwable);
        this.errorCode = errorCode.code();
        this.errorMessage = StringUtils.hasLength(message) ? message : errorCode.message();
    }
}