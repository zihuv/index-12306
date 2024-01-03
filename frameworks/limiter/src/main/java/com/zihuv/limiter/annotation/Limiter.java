package com.zihuv.limiter.annotation;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Limiter {

    String key() default "";

    int permitsPerSecond() default 1;

    String msg() default "系统服务繁忙";

}
