package com.zihuv.log.annotation;

import com.zihuv.convention.enums.OperateTypeEnum;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ILog {

    // ========== 模块字段 ==========

    /**
     * 操作模块（为空时，会尝试读取 {@link Tag#name()} 属性）
     */
    String module() default "";

    /**
     * 操作名（为空时，会尝试读取 {@link Operation#summary()} 属性）
     */
    String name() default "";

    /**
     * 操作分类
     */
    OperateTypeEnum type() default OperateTypeEnum.NULL;
}