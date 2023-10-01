package com.zihuv.ticketservice.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TrainStationVO {

    /**
     * 站序
     */
    private String sequence;

    /**
     * 站名
     */
    private String departure;

    /**
     * 到站时间
     */
    @JsonFormat(pattern = "HH:mm", timezone = "GMT+8")
    private LocalDateTime arrivalTime;

    /**
     * 出发时间
     */
    @JsonFormat(pattern = "HH:mm", timezone = "GMT+8")
    private LocalDateTime departureTime;

    /**
     * 停留时间
     */
    private Integer stopoverTime;
}