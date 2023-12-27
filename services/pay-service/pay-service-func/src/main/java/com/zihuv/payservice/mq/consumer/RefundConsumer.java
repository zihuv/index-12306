package com.zihuv.payservice.mq.consumer;

import cn.hutool.core.util.StrUtil;
import com.zihuv.base.util.JSON;
import com.zihuv.mq.domain.MessageWrapper;
import com.zihuv.orderservice.mq.event.RefundEvent;
import com.zihuv.payservice.common.constant.PayRocketMQConstant;
import com.zihuv.payservice.common.enums.PayStrategyEnum;
import com.zihuv.payservice.feign.PayFeign;
import com.zihuv.payservice.pojo.RefundParam;
import com.zihuv.web.utils.OpenFeignUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@RocketMQMessageListener(
        topic = PayRocketMQConstant.ORDER_REFUND_TOPIC,
        selectorExpression = PayRocketMQConstant.ORDER_REFUND_TAG,
        consumerGroup = PayRocketMQConstant.ORDER_REFUND_GROUP
)
public class RefundConsumer implements RocketMQListener<MessageWrapper<RefundEvent>> {

    private final PayFeign payFeign;

    @Override
    public void onMessage(MessageWrapper<RefundEvent> message) {
        log.info("[订单退款] 消息开始消费：{}", JSON.toJsonStr(message));
        RefundEvent refundEvent = message.getMessage();

        RefundParam refundParam = new RefundParam();
        // TODO 目前写死支付宝支付方式
        refundParam.setPayCode(PayStrategyEnum.ALIPAY.getPayCode());
        refundParam.setOrderNo(refundEvent.getOrderNo());
        refundParam.setRefundAmount(refundEvent.getRefundAmount());

        // TODO 幂等消费消息；自己给自己发请求？
        OpenFeignUtil.send(
                () -> payFeign.refund(refundParam),
                StrUtil.format("[订单退款] 消息消费失败，消息体：{}", refundEvent));
    }
}