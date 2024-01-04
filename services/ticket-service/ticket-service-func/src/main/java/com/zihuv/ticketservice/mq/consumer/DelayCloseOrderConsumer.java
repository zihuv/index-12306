package com.zihuv.ticketservice.mq.consumer;

import cn.hutool.core.util.StrUtil;
import com.zihuv.base.util.JSON;
import com.zihuv.idempotent.annotation.Idempotent;
import com.zihuv.idempotent.enums.IdempotentSceneEnum;
import com.zihuv.idempotent.enums.IdempotentTypeEnum;
import com.zihuv.mq.constant.MQSpELConstant;
import com.zihuv.mq.domain.MessageWrapper;
import com.zihuv.orderservice.common.enums.OrderStatusEnum;
import com.zihuv.orderservice.feign.OrderFeign;
import com.zihuv.orderservice.model.param.CloseOrderParam;
import com.zihuv.orderservice.mq.event.DelayCloseOrderEvent;
import com.zihuv.ticketservice.common.constant.TicketRocketMQConstant;
import com.zihuv.web.utils.OpenFeignUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import static com.zihuv.ticketservice.common.constant.IdempotentConstant.DELAY_ClOSE_ORDER_CONSUMER_KEY;

/**
 * 延迟关闭订单消费者
 */
@Slf4j
@Component
@RequiredArgsConstructor
@RocketMQMessageListener(
        topic = TicketRocketMQConstant.ORDER_DELAY_CLOSE_TOPIC_KEY,
        selectorExpression = TicketRocketMQConstant.ORDER_DELAY_CLOSE_TAG_KEY,
        consumerGroup = TicketRocketMQConstant.TICKET_DELAY_CLOSE_CG_KEY
)
public class DelayCloseOrderConsumer implements RocketMQListener<MessageWrapper<DelayCloseOrderEvent>> {

    private final OrderFeign orderFeign;
    private final RedisTemplate<String, Object> redisTemplate;

    @Idempotent(
            uniqueKeyPrefix = DELAY_ClOSE_ORDER_CONSUMER_KEY,
            key = MQSpELConstant.GET_MESSAGE_SPEL,
            type = IdempotentTypeEnum.SPEL,
            scene = IdempotentSceneEnum.MQ,
            keyTimeout = 1200L
    )
    @Override
    public void onMessage(MessageWrapper<DelayCloseOrderEvent> message) {
        log.info("[延迟关闭订单] 开始消费：{}", JSON.toJsonStr(message));
        DelayCloseOrderEvent delayCloseOrderEvent = message.getMessage();
        String orderNo = delayCloseOrderEvent.getOrderNo();

        CloseOrderParam closeOrderParam = new CloseOrderParam();
        closeOrderParam.setOrderNo(orderNo);
        closeOrderParam.setCloseCode(OrderStatusEnum.TIMEOUT.getCode());

        // 订单过期，关闭订单
        OpenFeignUtil.send(
                () -> orderFeign.closeOrder(closeOrderParam),
                StrUtil.format("[延迟关闭订单] 订单号：{} 远程调用订单服务失败", orderNo));

        // TODO 调用车票服务，回滚列车DB座位状态，回滚列车Cache余票
        // TODO 当订单过期时，不允许支付。检查 key
    }
}
