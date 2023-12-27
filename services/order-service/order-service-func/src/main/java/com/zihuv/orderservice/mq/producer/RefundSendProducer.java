package com.zihuv.orderservice.mq.producer;

import com.zihuv.mq.core.AbstractCommonSendProduceTemplate;
import com.zihuv.mq.domain.BaseMessage;
import com.zihuv.orderservice.mq.event.RefundEvent;
import org.springframework.stereotype.Component;

import static com.zihuv.orderservice.common.constant.OrderRocketMQConstant.ORDER_REFUND_TAG;
import static com.zihuv.orderservice.common.constant.OrderRocketMQConstant.ORDER_REFUND_TOPIC;

@Component
public class RefundSendProducer extends AbstractCommonSendProduceTemplate<RefundEvent> {

    @Override
    public BaseMessage<RefundEvent> buildBaseMessage(RefundEvent messageSendEvent) {
        BaseMessage<RefundEvent> baseMessage = new BaseMessage<>();
        baseMessage.setEventName("退款");
        baseMessage.setTopic(ORDER_REFUND_TOPIC);
        baseMessage.setTag(ORDER_REFUND_TAG);
        baseMessage.setKeys(messageSendEvent.getOrderNo());
        baseMessage.setMessageBody(messageSendEvent);
        baseMessage.setSentTimeout(2000L);
        baseMessage.setDelayLevel(0);
        return baseMessage;
    }
}