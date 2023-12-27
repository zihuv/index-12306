package com.zihuv.mq.domain;

import lombok.Data;

import java.util.UUID;

@Data
public class MessageWrapper<T> {

    /**
     * 消息体
     */
    private T message;

    public MessageWrapper() {
    }

    public MessageWrapper(T message) {
        this.message = message;
    }

    /**
     * 唯一标识，用于客户端幂等验证
     */
    private String uuid = UUID.randomUUID().toString();

    /**
     * 消息发送时间
     */
    private Long timestamp = System.currentTimeMillis();
}