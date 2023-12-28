package com.zihuv.orderservice.mq.transaction;

import com.zihuv.base.util.JSON;
import com.zihuv.cache.DistributedCache;
import com.zihuv.mq.domain.MessageWrapper;
import com.zihuv.orderservice.common.constant.RedisKeyConstant;
import com.zihuv.orderservice.common.enums.OrderStatusEnum;
import com.zihuv.orderservice.mq.event.DelayCloseOrderEvent;
import com.zihuv.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionState;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@RocketMQTransactionListener
public class DelayCloseOrderTransactionListenerImpl implements RocketMQLocalTransactionListener {

    private final OrderService orderService;
    private final DistributedCache distributedCache;

    @Override
    public RocketMQLocalTransactionState executeLocalTransaction(org.springframework.messaging.Message msg, Object arg) {
        String transactionId = "1";
        log.info("开始执行本地事务，事务编号：" + transactionId);
        // 消息中的 payload 在发送后会转换成 byte 数组
        MessageWrapper<?> messageWrapper = JSON.toBean((byte[]) msg.getPayload(), MessageWrapper.class);
        String jsonStr = JSON.toJsonStr(messageWrapper.getMessage());
        DelayCloseOrderEvent delayCloseOrderEvent = JSON.toBean(jsonStr, DelayCloseOrderEvent.class);
        try {
            // 处理业务
            orderService.saveOrder(delayCloseOrderEvent);
            // 修改订单状态
            distributedCache.put(RedisKeyConstant.ORDER_STATUS + delayCloseOrderEvent.getOrderNo(), OrderStatusEnum.NOT_PAY.getCode());
            log.info("本地事务执行完毕，事务编号：" + transactionId);
            return RocketMQLocalTransactionState.COMMIT;
        } catch (Exception e) {
            log.error("业务异常", e);
            log.error("本地事务执行失败，事务编号：" + transactionId);
            return RocketMQLocalTransactionState.ROLLBACK;
        }
    }

    @Override
    public RocketMQLocalTransactionState checkLocalTransaction(org.springframework.messaging.Message msg) {
        return null;
    }
}