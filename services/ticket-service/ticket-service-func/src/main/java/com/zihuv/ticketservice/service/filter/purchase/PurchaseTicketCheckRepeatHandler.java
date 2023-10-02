package com.zihuv.ticketservice.service.filter.purchase;

import com.zihuv.ticketservice.model.param.PurchaseTicketDetailParam;
import org.springframework.stereotype.Component;

@Component
public class PurchaseTicketCheckRepeatHandler implements PurchaseTicketChainFilter<PurchaseTicketDetailParam>{

    @Override
    public void handler(PurchaseTicketDetailParam requestParam) {
        // TODO 重复购买校验
    }

    @Override
    public int getOrder() {
        return 3;
    }
}