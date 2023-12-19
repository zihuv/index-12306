package com.zihuv.payservice.model.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PaymentOrder {
    private Integer paymentOrderId;
    private Integer orderId; // 关联商品订单的ID
    private double amount;
    private LocalDateTime paymentDate;
    private Integer paymentMethod;
}