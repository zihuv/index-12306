package com.zihuv.orderservice.service.impl;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zihuv.base.util.JSON;
import com.zihuv.cache.DistributedCache;
import com.zihuv.convention.exception.ServiceException;
import com.zihuv.orderservice.common.enums.OrderStatusEnum;
import com.zihuv.orderservice.mapper.OrderMapper;
import com.zihuv.orderservice.model.dto.PassengerInfoDTO;
import com.zihuv.orderservice.model.entity.Order;
import com.zihuv.orderservice.model.param.TicketOrderCreateParam;
import com.zihuv.orderservice.model.param.TicketOrderUpdateStatusParam;
import com.zihuv.orderservice.model.vo.OrderVO;
import com.zihuv.orderservice.mq.event.DelayCloseOrderEvent;
import com.zihuv.orderservice.mq.event.RefundEvent;
import com.zihuv.orderservice.mq.producer.DelayCloseOrderSendProducer;
import com.zihuv.orderservice.mq.producer.RefundSendProducer;
import com.zihuv.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    private final DistributedCache distributedCache;
    private final DelayCloseOrderSendProducer delayCloseOrderSendProducer;
    private final RefundSendProducer refundSendProducer;

    @Value("${index-12306.tail-number-strategy:userId}")
    private String tailNumberStrategy;

    private String getOrderNo(Long userId) {
        // 日期。如：231114
        String date = LocalDateTimeUtil.format(LocalDateTime.now(), "yyMMdd");
        // 时间戳。一天 86400000 毫秒，计算后缩短订单号长度
        String timestamp = String.valueOf(System.currentTimeMillis() % 86400000);
        // 尾号
        String tailNumber = "000000";
        if ("userId".equals(tailNumberStrategy)) {
            // 用户 id
            tailNumber = String.valueOf(userId % 10000);
        } else if ("random".equals(tailNumberStrategy)) {
            // 随机数
            int min = 100000;
            int max = 999999;
            tailNumber = String.valueOf(ThreadLocalRandom.current().nextInt(min, max + 1));
        }
        return date + timestamp + tailNumber;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public String createOrder(TicketOrderCreateParam requestParam) {
        // TODO 订单创建的幂等性，考虑加锁？
        String orderNo = this.getOrderNo(requestParam.getUserId());
        // 发送 RocketMQ 延时消息，指定时间后取消订单
        PassengerInfoDTO passengerInfoDTO = PassengerInfoDTO.builder()
                .userId(Long.parseLong(String.valueOf(requestParam.getUserId())))
                .realName(requestParam.getRealName())
                .build();

        DelayCloseOrderEvent delayCloseOrderEvent = DelayCloseOrderEvent.builder()
                .trainId(String.valueOf(requestParam.getTrainId()))
                .departure(requestParam.getDeparture())
                .arrival(requestParam.getArrival())
                .orderNo(orderNo)
                .passengerInfoDTO(passengerInfoDTO)
                .trainNumber(requestParam.getTrainNumber())
                .money(requestParam.getMoney())
                .arrivalTime(requestParam.getArrivalTime())
                .departureTime(requestParam.getDepartureTime())
                .build();
        // 创建订单并支付后延时关闭订单消息
        try {
            SendResult sendResult = delayCloseOrderSendProducer.sendTransactionalMessage(delayCloseOrderEvent);
            if (!Objects.equals(sendResult.getSendStatus(), SendStatus.SEND_OK)) {
                throw new ServiceException("[延迟队列] 投递延迟关闭订单消息队列失败");
            }
        } catch (Throwable e) {
            log.error("[延迟队列] 发送订单消息延迟队列发生错误，请求参数：{}", JSON.toJsonStr(requestParam), e);
            throw e;
        }
        log.info("[创建订单] 订单创建成功，参数：" + JSON.toJsonStr(requestParam));
        return orderNo;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveOrder(DelayCloseOrderEvent delayCloseOrderEvent) {
        // 创建订单
        Order order = new Order();
        String orderNo = this.getOrderNo(Long.parseLong(delayCloseOrderEvent.getOrderNo()));
        order.setOrderNo(orderNo);
        order.setUserId(String.valueOf(delayCloseOrderEvent.getPassengerInfoDTO().getUserId()));
        order.setRealName(delayCloseOrderEvent.getPassengerInfoDTO().getRealName());
        order.setTrainId(Long.parseLong(delayCloseOrderEvent.getTrainId()));
        order.setDeparture(delayCloseOrderEvent.getDeparture());
        order.setSource(0);
        order.setStatus(OrderStatusEnum.NOT_PAY.getCode());
        order.setMoney(delayCloseOrderEvent.getMoney());
        order.setOrderTime(LocalDateTime.now());
        order.setTrainNumber(delayCloseOrderEvent.getTrainNumber());
        order.setArrival(delayCloseOrderEvent.getArrival());
        order.setDepartureTime(delayCloseOrderEvent.getDepartureTime());
        order.setArrivalTime(delayCloseOrderEvent.getArrivalTime());
        this.save(order);
        // TODO 插入订单事务表

    }

    @Override
    public void cancelOrder(String orderNo) {
        cancelOrder(orderNo, OrderStatusEnum.CANCEL.getCode());
    }

    private void cancelOrder(String orderNo, Integer closeCode) {
        // TODO 加上分布式锁，修改数据库订单状态。也可以使用 select for update
        // TODO 校验订单状态顺序是为是正确的。
        LambdaQueryWrapper<Order> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Order::getOrderNo, orderNo);
        lqw.select(Order::getStatus);
        Order order = this.getOne(lqw);
        // 不允许在没有支付的情况下设置[退款]状态
        if (order != null && OrderStatusEnum.REFUNDED.getCode().equals(closeCode)) {
            if (order.getStatus().equals(OrderStatusEnum.SUCCESS.getCode())) {
                throw new ServiceException(StrUtil.format("[取消订单] 订单状态错误。原状态：{} ，修改的状态：{}", order.getStatus(), closeCode));
            }
        }

        LambdaUpdateWrapper<Order> luw = new LambdaUpdateWrapper<>();
        luw.eq(Order::getOrderNo, orderNo);
        luw.set(Order::getStatus, closeCode);
        this.update(luw);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void closeOrder(String orderNo, Integer closeCode) {
        // 取消订单 != 关闭订单
        // 取消订单是用户主动关闭的，而关闭订单代表该订单生命周期结束。
        // 当出现：取消订单，支付成功，订单超时，退款 时，需要关闭订单

        // 先修改订单状态为退款成功，再进行退款操作（相比多次退款，退款失败更容易被接受）
        // 关闭订单
        cancelOrder(orderNo, closeCode);
        // 如果是退款，修改完订单状态后还要把钱也退退了
        if (OrderStatusEnum.REFUNDED.getCode().equals(closeCode)) {
            Order order = this.getById(orderNo);
            RefundEvent refundEvent = new RefundEvent();
            refundEvent.setOrderNo(orderNo);
            // TODO 去流水数据库查询该订单的支付情况。或者直接丢个订单号过去？
            refundEvent.setRefundAmount(order.getMoney());

            // 将消息存储至本地消息表，并发送退款消息给支付服务（注意要添加事务）
            refundSendProducer.saveAndSendMessage(refundEvent);
            // TODO 退款掉单问题，订单状态修改和退款 使用定时任务查询。退款幂等，保证一个订单只退一次款
        }
    }
    // TODO 实现退款订单操作

    @Override
    public OrderVO queryOrder(String orderNo) {
        // TODO 需要加缓存？
        LambdaQueryWrapper<Order> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Order::getOrderNo, orderNo);
        Order order = this.getOne(lqw);

        OrderVO orderVO = new OrderVO();
        orderVO.setOrderNo(order.getOrderNo());
        orderVO.setRealName(order.getRealName());
        orderVO.setTrainId(orderVO.getTrainId());
        orderVO.setTrainNumber(order.getTrainNumber());
        orderVO.setDeparture(order.getDeparture());
        orderVO.setArrival(order.getArrival());
        orderVO.setSource(order.getSource());
        orderVO.setStatus(order.getStatus());
        orderVO.setOrderTime(order.getOrderTime());
        orderVO.setPayType(order.getPayType());
        orderVO.setPayTime(order.getPayTime());
        orderVO.setDepartureTime(order.getDepartureTime());
        orderVO.setArrivalTime(order.getArrivalTime());

        return orderVO;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateOrderStatus(TicketOrderUpdateStatusParam requestParam) {
        LambdaUpdateWrapper<Order> luw = new LambdaUpdateWrapper<>();
        luw.eq(Order::getOrderNo, requestParam.getOrderNo());
        luw.set(Order::getStatus, requestParam.getOrderNo());
        this.update(luw);
    }

}