package com.zihuv.ticketservice.model.dto;

import lombok.Data;

@Data
public class SeatChooseDTO {

    /**
     * 乘车人 id
     */
    private String passengerId;

    /**
     * 车厢号
     */
    private String carriageNumber;

    /**
     * 座位号
     */
    private String seatNumber;

    /**
     * 车票价格
     */
    private Integer price;
}