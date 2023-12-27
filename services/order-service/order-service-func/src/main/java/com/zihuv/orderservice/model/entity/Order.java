package com.zihuv.orderservice.model.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.zihuv.database.base.BaseDO;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("tb_order")
public class Order extends BaseDO {

    /**
     * id
     */
    @TableId
    private Long id;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 真实名称
     */
    private String realName;

    /**
     * 列车id
     */
    private Long trainId;

    /**
     * 列车车次
     */
    private String trainNumber;

    /**
     * 出发站点
     */
    private String departure;

    /**
     * 到达站点
     */
    private String arrival;

    /**
     * 订单来源
     */
    private Integer source;

    /**
     * 订单状态
     */
    private Integer status;

    /**
     * 下单时间
     */
    private LocalDateTime orderTime;

    /**
     * 支付方式
     */
    private Integer payType;

    /**
     * 订单金额
     */
    private String money;

    /**
     * 支付时间
     */
    private LocalDateTime payTime;

    /**
     * 出发时间
     */
    private LocalDateTime departureTime;

    /**
     * 出发时间
     */
    private LocalDateTime arrivalTime;
}