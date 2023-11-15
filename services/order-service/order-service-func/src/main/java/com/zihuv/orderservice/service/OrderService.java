package com.zihuv.orderservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zihuv.orderservice.model.entity.Order;
import com.zihuv.orderservice.model.param.TicketOrderCreateParam;

public interface OrderService extends IService<Order> {

    /**
     * 车票订单创建
     */
    void createTicketOrder(TicketOrderCreateParam requestParam);
}