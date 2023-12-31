package com.zihuv.payservice.mq.consumer;

import com.zihuv.base.util.JSON;
import com.zihuv.idempotent.annotation.Idempotent;
import com.zihuv.idempotent.enums.IdempotentSceneEnum;
import com.zihuv.idempotent.enums.IdempotentTypeEnum;
import com.zihuv.mq.constant.MQSpELConstant;
import com.zihuv.mq.domain.MessageWrapper;
import com.zihuv.orderservice.mq.event.RefundEvent;
import com.zihuv.payservice.common.constant.PayRocketMQConstant;
import com.zihuv.payservice.common.enums.PayStrategyEnum;
import com.zihuv.payservice.controller.PayController;
import com.zihuv.payservice.feign.PayFeign;
import com.zihuv.payservice.model.param.RefundParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

import static com.zihuv.payservice.common.constant.IdempotentConstant.REFUND_CONSUMER_KEY;

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
    private final PayController payController;

    @Idempotent(
            uniqueKeyPrefix = REFUND_CONSUMER_KEY,
            key = MQSpELConstant.GET_MESSAGE_SPEL,
            type = IdempotentTypeEnum.SPEL,
            scene = IdempotentSceneEnum.MQ,
            keyTimeout = 5000L
    )
    @Override
    public void onMessage(MessageWrapper<RefundEvent> message) {
        log.info("[订单退款] 消息开始消费：{}", JSON.toJsonStr(message));
        RefundEvent refundEvent = message.getMessage();

        RefundParam refundParam = new RefundParam();
        // TODO 目前写死支付宝支付方式
        refundParam.setPayCode(PayStrategyEnum.ALIPAY.getPayCode());
        refundParam.setOrderNo(refundEvent.getOrderNo());
        refundParam.setRefundAmount(refundEvent.getRefundAmount());

        payController.refund(refundParam);
    }
}