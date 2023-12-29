package com.zihuv.mq.core;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.zihuv.convention.exception.ServiceException;
import com.zihuv.mq.db.MessageMapper;
import com.zihuv.mq.domain.BaseMessage;
import com.zihuv.mq.domain.LocalMessage;
import com.zihuv.mq.domain.MessageWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * RocketMQ 抽象公共发送消息组件
 */
@Component
@Slf4j
public abstract class AbstractCommonSendProduceTemplate<T> {

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Autowired
    private MessageMapper messageMapper;

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
        return sendMessage(baseMessage);
    }

    /**
     * 消息事件通用发送（使用 baseMessage 来发消息）
     *
     * @param baseMessage 消息基础参数
     * @return 消息发送返回结果
     */
    public SendResult sendMessage(BaseMessage<T> baseMessage) {
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
            if (!Objects.equals(sendResult.getSendStatus(), SendStatus.SEND_OK)) {
                throw new ServiceException(StrUtil.format("[{}] 消息发送失败，消息体：{}", baseMessage.getEventName(), JSON.toJSONString(baseMessage)));
            }
            log.info("[{}] 消息发送结果：{}，消息 ID：{}，消息 Keys：{}", baseMessage.getEventName(), sendResult.getSendStatus(), sendResult.getMsgId(), baseMessage.getKeys());
        } catch (Exception ex) {
            log.error("[{}] 消息发送失败，消息体：{}", baseMessage.getEventName(), JSON.toJSONString(baseMessage), ex);
            throw ex;
        }
        return sendResult;
    }


    /**
     * 存储消息到本地消息表，并发送消息（需要开启事务）
     *
     * @param messageSendEvent 消息发送事件
     * @return 消息发送返回结果
     */
    public SendResult saveAndSendMessage(T messageSendEvent) {
        BaseMessage<T> baseMessage = buildBaseMessage(messageSendEvent);
        String destination = buildDestination(baseMessage);
        Message<?> message = buildMessage(baseMessage);
        // 将消息存入本地消息表
        LocalMessage localMessage = new LocalMessage();
        localMessage.setDestination(destination);
        localMessage.setMessageBody(message);
        messageMapper.insert(localMessage);
        // 发送消息
        return sendMessage(baseMessage);
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
            if (!Objects.equals(sendResult.getSendStatus(), SendStatus.SEND_OK)) {
                throw new ServiceException(StrUtil.format("[{}] 消息发送失败，消息体：{}", baseMessage.getEventName(), JSON.toJSONString(baseMessage)));
            }
            log.info("[{}] 消息发送结果：{}，消息 ID：{}，消息 Keys：{}", baseMessage.getEventName(), sendResult.getSendStatus(), sendResult.getMsgId(), baseMessage.getKeys());
        } catch (Exception ex) {
            log.error("[{}] 消息发送失败，消息体：{}", baseMessage.getEventName(), JSON.toJSONString(baseMessage), ex);
            throw ex;
        }
        return sendResult;
    }
}