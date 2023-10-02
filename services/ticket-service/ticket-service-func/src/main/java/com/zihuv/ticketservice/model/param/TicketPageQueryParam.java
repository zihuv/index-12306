package com.zihuv.ticketservice.model.param;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

@Data
public class TicketPageQueryParam {

    /**
     * 出发地 Code
     */
    private String fromStationCode;

    /**
     * 目的地 Code
     */
    private String toStationCode;

    /**
     * 出发日期
     */
    private String departureDate;

}