package com.zihuv.idempotent.core.service.param;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.zihuv.convention.exception.ServiceException;
import com.zihuv.idempotent.annotation.Idempotent;
import com.zihuv.idempotent.core.AbstractIdempotentExecuteHandler;
import com.zihuv.idempotent.core.IdempotentExecuteHandler;
import com.zihuv.idempotent.core.context.IdempotentContext;
import com.zihuv.idempotent.enums.IdempotentMQConsumeStatusEnum;
import com.zihuv.idempotent.exception.RepeatConsumeException;
import com.zihuv.idempotent.pojo.IdempotentParamWrapper;
import com.zihuv.mq.domain.MessageWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
public class IdempotentByMQExecuteHandler extends AbstractIdempotentExecuteHandler implements IdempotentExecuteHandler {

    private final RedisTemplate<String, Object> redisTemplate;
    private final static String WRAPPER = "wrapper:spEL:MQ";
    private final static int TIMEOUT = 600;

    @Override
    protected String buildLockKey(ProceedingJoinPoint joinPoint, Idempotent idempotent) {
        String message = "";
        boolean success = false;
        if (joinPoint.getArgs()[0] instanceof MessageWrapper<?> messageWrapper) {
            message = String.valueOf(messageWrapper.getMessage());
            if (StrUtil.isNotEmpty(message)) {
                success = true;
            }
        }
        if (!success) {
            throw new ServiceException("[幂等组件][MQ 幂等] 消息转换为字符串失败");
        }
        return StrUtil.format("idempotent:mq:{}:message:{}", idempotent.uniqueKeyPrefix(), calcArgsSHA256(message));
    }

    @Override
    public void handler(IdempotentParamWrapper idempotentParam) {
        String lockKey = idempotentParam.getLockKey();

        Boolean isNotExist = redisTemplate.opsForValue().setIfAbsent(lockKey, IdempotentMQConsumeStatusEnum.CONSUMING.getCode(), idempotentParam.getIdempotent().keyTimeout(), TimeUnit.SECONDS);
        // 该消息第一次被消费，能成功写入 redis，就不需要走下面的逻辑
        // 只有重复投递的消息走下面的逻辑
        if (!Boolean.TRUE.equals(isNotExist)) {
            String consumeStatus = String.valueOf(redisTemplate.opsForValue().get(lockKey));
            boolean error = IdempotentMQConsumeStatusEnum.isError(consumeStatus);
            if (error) {
                // 消息正在消费，MQ 将继续重新投递消息
                log.warn("[幂等模块][MQ] key：<{}> 消息正在消费中", idempotentParam.getLockKey());
            } else {
                // 消息已经正常消费，不再运行接下来的操作，正常放回结果，MQ 将不再投递消息
                log.info("[幂等模块][MQ] key：<{}> 消息已经消费完毕，已将该消息丢弃", idempotentParam.getLockKey());
            }
            throw new RepeatConsumeException(error);
        }
        IdempotentContext.put(WRAPPER, lockKey);
    }

    @Override
    public void exceptionProcessing() {
        // 出现异常，要将 key 删了，确保重投消息能进入方法正常消费
        String key = IdempotentContext.getKey(WRAPPER);
        if (key != null) {
            redisTemplate.opsForValue().getAndDelete(key);
        }
    }

    @Override
    public void postProcessing() {
        // 消息正常消费，将消息在 redis 中的 value 设置为“消费完毕”
        String key = IdempotentContext.getKey(WRAPPER);
        if (key != null) {
            redisTemplate.opsForValue().set(key, IdempotentMQConsumeStatusEnum.CONSUMED.getCode(), TIMEOUT, TimeUnit.SECONDS);
        }
    }

    private String calcArgsSHA256(String text) {
        return DigestUtil.sha256Hex(text);
    }
}