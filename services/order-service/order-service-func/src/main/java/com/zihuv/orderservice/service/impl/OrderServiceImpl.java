package com.zihuv.orderservice.service.impl;

import cn.hutool.core.date.LocalDateTimeUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zihuv.base.util.JSON;
import com.zihuv.cache.DistributedCache;
import com.zihuv.convention.exception.ServiceException;
import com.zihuv.orderservice.common.constant.RedisKeyConstant;
import com.zihuv.orderservice.common.enums.OrderStatusEnum;
import com.zihuv.orderservice.model.dto.PassengerInfoDTO;
import com.zihuv.orderservice.model.entity.Order;
import com.zihuv.orderservice.model.vo.OrderVO;
import com.zihuv.orderservice.mapper.OrderMapper;
import com.zihuv.orderservice.model.param.TicketOrderCreateParam;
import com.zihuv.orderservice.mq.event.DelayCloseOrderEvent;
import com.zihuv.orderservice.mq.producer.DelayCloseOrderSendProducer;
import com.zihuv.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    private final DistributedCache distributedCache;
    private final RedisTemplate<String, Object> redisTemplate;
    private final DelayCloseOrderSendProducer delayCloseOrderSendProducer;

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

    @Override
    public String createOrder(TicketOrderCreateParam requestParam) {
        // TODO 订单创建的幂等性，考虑加锁？
        Order order = new Order();
        String orderNo = this.getOrderNo(requestParam.getUserId());
        order.setOrderNo(orderNo);
        order.setUserId(String.valueOf(requestParam.getUserId()));
        order.setRealName(requestParam.getRealName());
        order.setTrainId(requestParam.getTrainId());
        order.setDeparture(requestParam.getDeparture());
        order.setSource(0);
        order.setStatus(OrderStatusEnum.NOT_PAY.getCode());
        order.setOrderTime(LocalDateTime.now());
        order.setTrainNumber(requestParam.getTrainNumber());
        order.setArrival(requestParam.getArrival());
        order.setDepartureTime(requestParam.getDepartureTime());
        order.setArrivalTime(requestParam.getArrivalTime());

        this.save(order);
        // 修改订单状态
        distributedCache.put(RedisKeyConstant.ORDER_STATUS + orderNo, OrderStatusEnum.NOT_PAY.getCode());
        // 发送 RocketMQ 延时消息，指定时间后取消订单
        PassengerInfoDTO passengerInfoDTO = PassengerInfoDTO.builder()
                .userId(Long.parseLong(order.getUserId()) )
                .realName(requestParam.getRealName())
                .build();

        DelayCloseOrderEvent delayCloseOrderEvent = DelayCloseOrderEvent.builder()
                .trainId(String.valueOf(requestParam.getTrainId()))
                .departure(requestParam.getDeparture())
                .arrival(requestParam.getArrival())
                .orderNo(order.getOrderNo())
                .passengerInfoDTO(passengerInfoDTO)
                .build();
        // 创建订单并支付后延时关闭订单消息
        try {
            SendResult sendResult = delayCloseOrderSendProducer.sendMessage(delayCloseOrderEvent);
            if (!Objects.equals(sendResult.getSendStatus(), SendStatus.SEND_OK)) {
                throw new ServiceException("投递延迟关闭订单消息队列失败");
            }
        } catch (Throwable e) {
            log.error("延迟关闭订单消息队列发送错误，请求参数：{}", JSON.toJsonStr(requestParam), e);
            throw e;
        }

        log.info("[创建订单] 订单信息：{}", order);
        return orderNo;
    }

    @Override
    public void cancelOrder(String orderNo) {
        // 在 db 中更新
        LambdaUpdateWrapper<Order> luw = new LambdaUpdateWrapper<>();
        luw.eq(Order::getOrderNo, orderNo);
        luw.set(Order::getStatus, OrderStatusEnum.CANCEL.getCode());
        this.update(luw);
    }

    @Override
    public void closeOrder(String orderNo) {
        // 取消订单 != 关闭订单
        // 取消订单是用户主动关闭的，而关闭订单代表该订单生命周期结束。
        // 当出现：取消订单，支付成功，订单过期 时，需要关闭订单
        cancelOrder(orderNo);
        distributedCache.put(RedisKeyConstant.ORDER_STATUS + orderNo, OrderStatusEnum.CLOSE.getCode());
        // TODO 加上分布式锁，修改数据库订单状态

    }

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


}