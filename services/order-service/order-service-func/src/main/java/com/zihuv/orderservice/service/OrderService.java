package com.zihuv.orderservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zihuv.orderservice.model.entity.Order;
import com.zihuv.orderservice.model.param.TicketOrderCreateParam;
import com.zihuv.orderservice.model.vo.OrderVO;

public interface OrderService extends IService<Order> {

    /**
     * 创建车票订单
     *
     * @return 订单号
     */
    String createOrder(TicketOrderCreateParam requestParam);

    /**
     * 取消订单
     */
    void cancelOrder(String orderNo);

    /**
     * 关闭订单
     */
    void closeOrder(String orderNo);

    /**
     * 查询订单
     *
     * @return 订单 VO
     */
    OrderVO queryOrder(String orderNo);


}