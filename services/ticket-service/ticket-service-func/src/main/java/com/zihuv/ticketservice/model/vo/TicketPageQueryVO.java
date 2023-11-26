package com.zihuv.ticketservice.model.vo;

import com.zihuv.ticketservice.model.dto.SeatTypeCountDTO;
import lombok.Data;

import java.util.List;

@Data
public class TicketPageQueryVO {

    /**
     * 列车 id
     */
    private Long trainId;

    /**
     * 列车车次 eg：G35
     */
    private String trainNumber;

    /**
     * 列车类型 0：高铁 1：动车 2：普通车
     */
    private Integer trainType;

    /**
     * 列车标签 0：复兴号 1：智能动车组 2：静音车厢 3：支持选铺
     */
    private String trainTag;

    /**
     * 列车品牌类型 0：GC-高铁/城际 1：D-动车 2：Z-直达 3：T-特快 4：K-快速 5：其他 6：复兴号 7：智能动车组
     */
    private String trainBrand;

    /**
     * 起始站
     */
    private String startStation;

    /**
     * 终点站
     */
    private String endStation;

    /**
     * 起始城市
     */
    private String startRegion;

    /**
     * 终点城市
     */
    private String endRegion;

    /**
     * 出发时间
     */
    private String departureTime;

    /**
     * 到达时间
     */
    private String arrivalTime;

    /**
     * 全程花费时间
     */
    private String spendTime;

    /**
     * 座位类型与对应数量集合
     */
    List<SeatTypeCountDTO> seatTypeCountDTOList;
}