package com.zihuv.mq.core;

import com.alibaba.fastjson.JSON;
import com.zihuv.mq.domain.BaseMessage;
import com.zihuv.mq.domain.MessageWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

/**
 * RocketMQ 抽象公共发送消息组件
 */
@Slf4j
public abstract class AbstractCommonSendProduceTemplate<T> {

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    /**
     * 构建消息基础参数（默认情况下，仅需重写该方法即可。发送消息有模板模式）
     *
     * @param messageSendEvent 消息事件
     * @return 消息基础参数
     */
    public abstract BaseMessage<T> buildBaseMessage(T messageSendEvent);

    /**
     * 构建消息发送目的地
     *
     * @param baseMessage 消息基础参数
     * @return 消息发送目的地
     */
    public String buildDestination(BaseMessage<T> baseMessage) {
        return baseMessage.getTopic() + ":" + baseMessage.getTag();
    }

    /**
     * 构建消息
     *
     * @param baseMessage 消息基础参数
     * @return 消息
     */
    public Message<?> buildMessage(BaseMessage<T> baseMessage) {
        return MessageBuilder
                .withPayload(new MessageWrapper<>(baseMessage.getMessageBody()))
                .setHeader(MessageConst.PROPERTY_KEYS, baseMessage.getKeys())
                .build();
    }

    /**
     * 消息事件通用发送（模板模式）
     *
     * @param messageSendEvent 消息发送事件
     * @return 消息发送返回结果
     */
    public SendResult sendMessage(T messageSendEvent) {
        BaseMessage<T> baseMessage = buildBaseMessage(messageSendEvent);
        String destination = buildDestination(baseMessage);
        Message<?> message = buildMessage(baseMessage);

        SendResult sendResult;
        try {
            sendResult = rocketMQTemplate.syncSend(
                    destination,
                    message,
                    baseMessage.getSentTimeout(),
                    baseMessage.getDelayLevel()
            );
            log.info("[{}] 消息发送结果：{}，消息 ID：{}，消息 Keys：{}", baseMessage.getEventName(), sendResult.getSendStatus(), sendResult.getMsgId(), baseMessage.getKeys());
        } catch (Exception ex) {
            log.error("[{}] 消息发送失败，消息体：{}", baseMessage.getEventName(), JSON.toJSONString(messageSendEvent), ex);
            throw ex;
        }
        return sendResult;
    }

    /**
     * 事务消息事件通用发送（模板模式）
     *
     * @param messageSendEvent 消息发送事件
     * @return 消息发送返回结果
     */
    public SendResult sendTransactionalMessage(T messageSendEvent) {
        BaseMessage<T> baseMessage = buildBaseMessage(messageSendEvent);
        String destination = buildDestination(baseMessage);
        Message<?> message = buildMessage(baseMessage);

        SendResult sendResult;
        try {
            sendResult = rocketMQTemplate.sendMessageInTransaction(
                    destination,
                    message,
                    null);
            log.info("[事务消息: {}] half 消息发送成功", baseMessage.getEventName());
            log.info("[{}] 消息发送结果：{}，消息 ID：{}，消息 Keys：{}", baseMessage.getEventName(), sendResult.getSendStatus(), sendResult.getMsgId(), baseMessage.getKeys());
        } catch (Exception ex) {
            log.error("[{}] 消息发送失败，消息体：{}", baseMessage.getEventName(), JSON.toJSONString(messageSendEvent), ex);
            throw ex;
        }
        return sendResult;
    }
}