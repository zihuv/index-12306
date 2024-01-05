package com.zihuv.idempotent.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class RepeatConsumeException extends RuntimeException{

    private final Boolean error;
}