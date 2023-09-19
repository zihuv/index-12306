package com.zihuv.idempotent.enums;

/**
 * 幂等验证类型枚举（决定 key 的生成策略）
 */
public enum IdempotentTypeEnum {
    
    /**
     * 基于方法参数方式验证
     */
    PARAM,
    
    /**
     * 基于 SpEL 表达式方式验证
     */
    SPEL,

    /**
     * 基于 方法参数和 SpEL 表达式方式验证
     */
    PARAM_SPEL
}
