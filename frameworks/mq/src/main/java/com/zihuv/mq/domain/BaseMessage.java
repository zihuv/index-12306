package com.zihuv.mq.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 消息基础参数
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public final class BaseMessage<T> {

    /**
     * 事件名称（用于打印日志）
     */
    private String eventName;

    /**
     * 主题
     */
    private String topic;

    /**
     * 标签
     */
    private String tag;

    /**
     * 业务唯一标识符（如：订单号）
     */
    private String keys;

    /**
     * 消息内容
     */
    private T messageBody;

    /**
     * 发送消息超时时间
     */
    private Long sentTimeout = 2000L;

    /**
     * 延迟消息（0 - 不设置为延迟消息）
     */
    private Integer delayLevel = 0;
}