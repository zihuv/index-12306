package com.zihuv.ticketservice.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zihuv.DistributedCache;
import com.zihuv.convention.exception.ServiceException;
import com.zihuv.designpattern.chain.AbstractChainContext;
import com.zihuv.ticketservice.common.enums.TicketChainMarkEnum;
import com.zihuv.ticketservice.model.entity.Ticket;
import com.zihuv.ticketservice.model.param.PurchaseTicketDetailParam;
import com.zihuv.ticketservice.model.vo.TicketOrderDetailVO;
import com.zihuv.ticketservice.service.TicketService;
import com.zihuv.ticketservice.mapper.TicketMapper;
import com.zihuv.ticketservice.tokenbucket.TicketAvailabilityTokenBucket;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TicketServiceImpl extends ServiceImpl<TicketMapper, Ticket> implements TicketService{

    private final DistributedCache distributedCache;
    private final AbstractChainContext<PurchaseTicketDetailParam> purchaseTicketAbstractChainContext;
    private final TicketAvailabilityTokenBucket ticketAvailabilityTokenBucket;

    @Override
    public TicketOrderDetailVO purchaseTickets(PurchaseTicketDetailParam purchaseTicket) {
        // 责任链模式，验证 1：参数不为空 2：参数是否有效 3：乘客是否重复买票
        purchaseTicketAbstractChainContext.handler(TicketChainMarkEnum.TRAIN_PURCHASE_TICKET_FILTER.name(), purchaseTicket);
        boolean tokenResult = ticketAvailabilityTokenBucket.takeTokenFromBucket(purchaseTicket);
        if (!tokenResult) {
            throw new ServiceException("列车站点已无余票");
        }
        return null;
    }
}




