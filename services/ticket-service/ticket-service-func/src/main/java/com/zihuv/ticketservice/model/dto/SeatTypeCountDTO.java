package com.zihuv.ticketservice.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 座位类型和座位数量实体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeatTypeCountDTO {

    /**
     * 座位类型
     */
    private Integer seatType;

    /**
     * 座位类型 - 对应数量
     */
    private Integer seatCount;
}