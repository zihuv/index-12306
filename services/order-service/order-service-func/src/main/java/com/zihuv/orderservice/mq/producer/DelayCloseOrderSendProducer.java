package com.zihuv.orderservice.mq.producer;

import cn.hutool.core.util.StrUtil;
import com.zihuv.mq.core.AbstractCommonSendProduceTemplate;
import com.zihuv.mq.domain.BaseSendExtendDTO;
import com.zihuv.mq.domain.MessageWrapper;
import com.zihuv.orderservice.mq.event.DelayCloseOrderEvent;
import com.zihuv.orderservice.common.constant.OrderRocketMQConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
public class DelayCloseOrderSendProducer extends AbstractCommonSendProduceTemplate<DelayCloseOrderEvent> {

    @Value("${index-12306.mq-delay-level:14}")
    private Integer delayLevel;

    public DelayCloseOrderSendProducer(RocketMQTemplate rocketMQTemplate) {
        super(rocketMQTemplate);
    }

    public BaseSendExtendDTO buildBaseSendExtendParam(DelayCloseOrderEvent messageSendEvent) {
        return BaseSendExtendDTO.builder()
                .eventName("延迟关闭订单")
                .keys(messageSendEvent.getOrderNo())
                .topic(OrderRocketMQConstant.ORDER_DELAY_CLOSE_TOPIC_KEY)
                .tag(OrderRocketMQConstant.ORDER_DELAY_CLOSE_TAG_KEY)
                .sentTimeout(2000L)
                // RocketMQ 延迟消息级别，14 代表设置 10 分钟延迟消息（详情：https://rocketmq.apache.org/zh/docs/4.x/producer/04message3）
                .delayLevel(delayLevel)
                .build();
    }

    public Message<?> buildMessage(DelayCloseOrderEvent messageSendEvent, BaseSendExtendDTO requestParam) {
        String keys = StrUtil.isEmpty(requestParam.getKeys()) ? UUID.randomUUID().toString() : requestParam.getKeys();
        return MessageBuilder
                .withPayload(new MessageWrapper<>(requestParam.getKeys(), messageSendEvent))
                .setHeader(MessageConst.PROPERTY_KEYS, keys)
                .setHeader(MessageConst.PROPERTY_TAGS, requestParam.getTag())
                .build();
    }
}