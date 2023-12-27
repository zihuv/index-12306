package com.zihuv.orderservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zihuv.orderservice.model.entity.Order;
import com.zihuv.orderservice.model.param.TicketOrderCreateParam;
import com.zihuv.orderservice.model.param.TicketOrderUpdateStatusParam;
import com.zihuv.orderservice.model.vo.OrderVO;
import com.zihuv.orderservice.mq.event.DelayCloseOrderEvent;

public interface OrderService extends IService<Order> {

    /**
     * 创建车票订单
     *
     * @return 订单号
     */
    String createOrder(TicketOrderCreateParam requestParam);

    /**
     * 存储订单
     *
     * @param delayCloseOrderEvent 订单事件
     */
    void saveOrder(DelayCloseOrderEvent delayCloseOrderEvent);

    /**
     * 取消订单
     */
    void cancelOrder(String orderNo);

    /**
     * 关闭订单
     */
    void closeOrder(String orderNo, Integer closeCode);

    /**
     * 查询订单
     *
     * @return 订单 VO
     */
    OrderVO queryOrder(String orderNo);


    /**
     * 更新订单状态
     *
     * @param requestParam 修改状态的请求参数
     */
    void updateOrderStatus(TicketOrderUpdateStatusParam requestParam);
}