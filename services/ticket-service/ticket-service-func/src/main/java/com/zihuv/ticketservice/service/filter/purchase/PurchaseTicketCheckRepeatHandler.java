package com.zihuv.ticketservice.service.filter.purchase;

import com.zihuv.ticketservice.model.param.TicketPurchaseDetailParam;
import org.springframework.stereotype.Component;

@Component
public class PurchaseTicketCheckRepeatHandler implements PurchaseTicketChainFilter<TicketPurchaseDetailParam>{

    @Override
    public void handler(TicketPurchaseDetailParam requestParam) {
        // TODO 重复购买校验
    }

    @Override
    public int getOrder() {
        return 3;
    }
}