package com.zihuv.idempotent.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NoStackTraceException extends RuntimeException {

    public NoStackTraceException(String message) {
        super(message, null, false, false);
        log.warn(message);
    }
}