package com.zihuv.ticketservice.service.filter.purchase;

import com.zihuv.designpattern.chain.AbstractChainHandler;
import com.zihuv.ticketservice.common.enums.TicketChainMarkEnum;
import com.zihuv.ticketservice.model.param.TicketPurchaseDetailParam;

/**
 * 列车购买车票过滤器
 */
public interface PurchaseTicketChainFilter<PurchaseTicketReqDTO> extends AbstractChainHandler<TicketPurchaseDetailParam> {

    @Override
    default String mark() {
        return TicketChainMarkEnum.TRAIN_PURCHASE_TICKET_FILTER.name();
    }
}
