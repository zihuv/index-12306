package com.zihuv.idempotent.core.aspect;

import com.zihuv.idempotent.annotation.Idempotent;
import com.zihuv.idempotent.core.IdempotentExecuteHandler;
import com.zihuv.idempotent.core.context.IdempotentContext;
import com.zihuv.idempotent.core.factory.IdempotentExecuteHandlerFactory;
import com.zihuv.idempotent.exception.NoStackTraceException;
import com.zihuv.idempotent.exception.RepeatConsumeException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

@Slf4j
@Aspect
public class IdempotentAspect {

    @Around("@annotation(idempotent)")
    public Object idempotentHandler(ProceedingJoinPoint joinPoint, Idempotent idempotent) throws Throwable {
        IdempotentExecuteHandler instance = IdempotentExecuteHandlerFactory.getInstance(idempotent.scene(), idempotent.type());

        Object resultObj;
        try {
            instance.execute(joinPoint, idempotent);
            resultObj = joinPoint.proceed();
            instance.postProcessing();
        } catch (Exception e) {
            if (e instanceof RepeatConsumeException ex) {
                          /*
              处理 MQ 幂等性
              MQ 幂等性有三种情况：
              1. 第一次消费。正常消费，不会抛任何异常
              2. 消息消费中。对于重新投递的消息，要将异常抛出，让 MQ 继续重新投递消息，知道消息正常消费完毕为止。如果消息消费
              过程出现了异常，需要将 redis 中的幂等 key 删除，保证之后的重投消息能被放进来消费
              3. 消息消费完毕。对于重投消息，不用再抛异常，直接返回 null，MQ 将停止重投消息，并结束方法继续往下执行，完成幂等
             */
                if (!ex.getError()) {
                    return null;
                }
                throw new NoStackTraceException("[幂等消息] 消息正在消费中，抛出异常使 MQ 重新投递消息");
            }
            instance.exceptionProcessing();
            throw e;
        } finally {
            // 将幂等上下文清空，避免对下个请求获取上下文造成影响
            IdempotentContext.clean();
        }
        return resultObj;
    }
}