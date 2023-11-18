package com.zihuv.ticketservice.model.param;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

@Data
public class TicketPageQueryParam {

    /**
     * 出发地
     */
    private String departure;

    /**
     * 目的地
     */
    private String arrival;

    /**
     * 出发日期
     */
    private String departureDate;

}