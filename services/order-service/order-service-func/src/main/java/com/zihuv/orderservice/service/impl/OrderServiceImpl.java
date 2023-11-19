package com.zihuv.orderservice.service.impl;

import cn.hutool.core.date.LocalDateTimeUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zihuv.orderservice.common.enums.OrderStatusEnum;
import com.zihuv.orderservice.mapper.OrderMapper;
import com.zihuv.orderservice.model.entity.Order;
import com.zihuv.orderservice.model.param.TicketOrderCreateParam;
import com.zihuv.orderservice.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

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

        log.info("创建订单成功：{}", order);

        return orderNo;
    }

    @Override
    public void cancelOrder(TicketOrderCreateParam requestParam) {

    }

    @Override
    public void queryOrder(TicketOrderCreateParam requestParam) {

    }
}