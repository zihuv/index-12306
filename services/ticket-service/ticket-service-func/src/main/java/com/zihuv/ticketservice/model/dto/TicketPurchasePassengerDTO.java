package com.zihuv.ticketservice.model.dto;

import lombok.Data;

/**
 * 购票乘车人信息
 */
@Data
public class TicketPurchasePassengerDTO {

    /**
     * 乘车人 ID
     */
    private String passengerId;

    /**
     * 座位类型
     */
    private Integer seatType;

    /**
     * 选择座位（eg：A,B,C,D ）
     */
    private Character chooseSeat;
}