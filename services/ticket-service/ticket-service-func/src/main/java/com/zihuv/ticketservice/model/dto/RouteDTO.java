package com.zihuv.ticketservice.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 经过的站点路线
 */
@Data
public class RouteDTO {

    /**
     * 出发站点
     */
    @JsonProperty("departure")
    private String startStation;

    /**
     * 目的站点
     */
    @JsonProperty("arrival")
    private String endStation;
}