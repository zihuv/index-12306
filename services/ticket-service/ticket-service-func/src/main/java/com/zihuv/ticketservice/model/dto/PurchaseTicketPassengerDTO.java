package com.zihuv.ticketservice.model.dto;

import lombok.Data;

/**
 * 购票乘车人信息
 */
@Data
public class PurchaseTicketPassengerDTO {

    /**
     * 乘车人 ID
     */
    private String passengerId;

    /**
     * 座位类型
     */
    private Integer seatType;
}