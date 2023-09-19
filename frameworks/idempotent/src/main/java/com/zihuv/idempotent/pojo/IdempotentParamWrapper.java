package com.zihuv.idempotent.pojo;

import com.zihuv.idempotent.annotation.Idempotent;
import com.zihuv.idempotent.enums.IdempotentTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;

/**
 * 幂等参数包装
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public final class IdempotentParamWrapper {

    /**
     * 幂等注解
     */
    private Idempotent idempotent;

    /**
     * AOP 处理连接点
     */
    private ProceedingJoinPoint joinPoint;

    /**
     * 锁的 key，{@link IdempotentTypeEnum#PARAM}
     */
    private String lockKey;
}