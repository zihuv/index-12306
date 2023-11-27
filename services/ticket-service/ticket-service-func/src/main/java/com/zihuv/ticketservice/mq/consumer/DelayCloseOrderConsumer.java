package com.zihuv.ticketservice.mq.consumer;

import cn.hutool.core.util.StrUtil;
import com.zihuv.base.util.JSON;
import com.zihuv.convention.exception.ServiceException;
import com.zihuv.convention.result.Result;
import com.zihuv.mq.domain.MessageWrapper;
import com.zihuv.orderservice.feign.OrderFeign;
import com.zihuv.orderservice.mq.event.DelayCloseOrderEvent;
import com.zihuv.ticketservice.common.constant.TicketRocketMQConstant;
import com.zihuv.ticketservice.model.dto.RouteDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
public final class DelayCloseOrderConsumer implements RocketMQListener<MessageWrapper<DelayCloseOrderEvent>> {

    public final OrderFeign orderFeign;

    @Override
    public void onMessage(MessageWrapper<DelayCloseOrderEvent> message) {
        log.info("[延迟关闭订单] 开始消费：{}", JSON.toJsonStr(message));
        DelayCloseOrderEvent delayCloseOrderEvent = message.getMessage();
        String orderNo = delayCloseOrderEvent.getOrderNo();

        Result<?> closedTickOrder;
        try {
            // 订单过期，关闭订单
            closedTickOrder = orderFeign.closeOrder(orderNo);
            if (!closedTickOrder.isSuccess()) {
                throw new ServiceException(StrUtil.format("[延迟关闭订单] 订单号：{} 远程调用订单服务失败", orderNo));
            }
        } catch (Exception e) {
            log.error("[延迟关闭订单] 订单号：{} 远程调用订单服务失败", orderNo);
            throw e;
        }

        // TODO 回滚列车DB座位状态，回滚列车Cache余票
        // TODO 当订单过期时，不允许支付。检查 key
    }
}
