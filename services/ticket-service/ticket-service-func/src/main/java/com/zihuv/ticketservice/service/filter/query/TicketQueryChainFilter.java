package com.zihuv.ticketservice.service.filter.query;

import com.zihuv.designpattern.chain.AbstractChainHandler;
import com.zihuv.ticketservice.common.enums.TicketChainMarkEnum;
import com.zihuv.ticketservice.model.param.TicketPageQueryParam;

/**
 * 列车车票查询过滤器
 */
public interface TicketQueryChainFilter<T extends TicketPageQueryParam> extends AbstractChainHandler<TicketPageQueryParam> {

    @Override
    default String mark() {
        return TicketChainMarkEnum.TRAIN_QUERY_FILTER.name();
    }
}