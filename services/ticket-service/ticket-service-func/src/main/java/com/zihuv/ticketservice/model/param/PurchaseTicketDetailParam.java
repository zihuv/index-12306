package com.zihuv.ticketservice.model.param;

import com.zihuv.ticketservice.model.dto.PurchaseTicketPassengerDTO;
import lombok.Data;

import java.util.List;

/**
 * 购票请求参数
 */
@Data
public class PurchaseTicketDetailParam {

    /**
     * 车次 ID
     */
    private String trainId;


    /**
     * 乘车人
     */
    private List<PurchaseTicketPassengerDTO> passengers;

    /**
     * 选择座位
     */
    private List<String> chooseSeats;

    /**
     * 出发站点
     */
    private String departure;

    /**
     * 到达站点
     */
    private String arrival;
}