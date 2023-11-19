package com.zihuv.orderservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zihuv.orderservice.model.entity.Order;
import com.zihuv.orderservice.model.param.TicketOrderCreateParam;

public interface OrderService extends IService<Order> {

    /**
     * 车票订单创建
     *
     * @return 订单号
     */
    String createOrder(TicketOrderCreateParam requestParam);

    void cancelOrder(TicketOrderCreateParam requestParam);

    void queryOrder(TicketOrderCreateParam requestParam);
}