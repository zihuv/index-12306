package com.zihuv.ticketservice.model.param;

import com.zihuv.ticketservice.model.dto.TicketPurchasePassengerDTO;
import lombok.Data;

import java.util.List;

/**
 * 购票请求参数
 */
@Data
public class TicketPurchaseDetailParam {

    /**
     * 车次 ID
     */
    private String trainId;

    /**
     * 乘车人
     */
    private List<TicketPurchasePassengerDTO> passengers;

    /**
     * 出发站点
     */
    private String departure;

    /**
     * 到达站点
     */
    private String arrival;
}