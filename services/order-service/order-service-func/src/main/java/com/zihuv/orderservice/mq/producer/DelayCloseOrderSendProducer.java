package com.zihuv.orderservice.mq.producer;

import com.zihuv.mq.core.AbstractCommonSendProduceTemplate;
import com.zihuv.mq.domain.BaseMessage;
import com.zihuv.orderservice.mq.event.DelayCloseOrderEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static com.zihuv.orderservice.common.constant.OrderRocketMQConstant.ORDER_DELAY_CLOSE_TAG_KEY;
import static com.zihuv.orderservice.common.constant.OrderRocketMQConstant.ORDER_DELAY_CLOSE_TOPIC_KEY;

@Component
public class DelayCloseOrderSendProducer extends AbstractCommonSendProduceTemplate<DelayCloseOrderEvent> {

    @Value("${index-12306.mq-delay-level:14}")
    private Integer delayLevel;

    @Override
    public BaseMessage<DelayCloseOrderEvent> buildBaseMessage(DelayCloseOrderEvent messageSendEvent) {
        BaseMessage<DelayCloseOrderEvent> baseMessage = new BaseMessage<>();
        baseMessage.setEventName("延迟关闭订单");
        baseMessage.setTopic(ORDER_DELAY_CLOSE_TOPIC_KEY);
        baseMessage.setTag(ORDER_DELAY_CLOSE_TAG_KEY);
        baseMessage.setKeys(messageSendEvent.getOrderNo());
        baseMessage.setMessageBody(messageSendEvent);
        baseMessage.setSentTimeout(2000L);
        baseMessage.setDelayLevel(delayLevel);
        return baseMessage;
    }
}