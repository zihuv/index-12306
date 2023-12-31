package com.zihuv.idempotent.annotation;

import com.zihuv.idempotent.enums.IdempotentSceneEnum;
import com.zihuv.idempotent.enums.IdempotentTypeEnum;

import java.lang.annotation.*;

/**
 * 幂等注解
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Idempotent {

    /**
     * 设置防重令牌 Key 前缀，MQ 幂等去重可选设置（必选）
     */
    String uniqueKeyPrefix() default "default";

    /**
     * 幂等Key，只有在 {@link Idempotent#type()} 为 {@link IdempotentTypeEnum#SPEL} 时生效（可选）
     */
    String key() default "";

    /**
     * 设置防重令牌 Key 过期时间，单位秒，默认 5 秒，MQ 幂等去重可选设置（可选）
     */
    long keyTimeout() default 5L;

    /**
     * 触发幂等失败逻辑时，返回的错误提示信息（可选）
     */
    String message() default "您操作太快，请稍后再试";

    /**
     * 验证幂等场景，支持多种 {@link IdempotentSceneEnum}（必选）
     */
    IdempotentSceneEnum scene() default IdempotentSceneEnum.RESTAPI;

    /**
     * 验证幂等类型，支持多种幂等方式（决定生成的 key 的规则）
     * RestAPI 建议使用 {@link IdempotentTypeEnum#PARAM}
     * 其它类型幂等验证，使用 {@link IdempotentTypeEnum#SPEL}（必选）
     */
    IdempotentTypeEnum type() default IdempotentTypeEnum.PARAM;

}
