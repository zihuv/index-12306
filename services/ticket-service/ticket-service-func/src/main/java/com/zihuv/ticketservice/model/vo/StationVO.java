package com.zihuv.ticketservice.model.vo;

import lombok.Data;

@Data
public class StationVO {

    /**
     * 车站名称
     */
    private String name;

    /**
     * 城市名称
     */
    private String regionName;

    /**
     * 拼音
     */
    private String spell;

    /**
     * 地区编码
     */
    private String code;
}