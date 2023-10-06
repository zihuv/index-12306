package com.zihuv.ticketservice.service.filter.purchase;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.CharUtil;
import cn.hutool.core.util.StrUtil;
import com.zihuv.convention.exception.ClientException;
import com.zihuv.ticketservice.model.dto.TicketPurchasePassengerDTO;
import com.zihuv.ticketservice.model.param.TicketPurchaseDetailParam;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class PurchaseTicketCheckParamNotNullHandler implements PurchaseTicketChainFilter<TicketPurchaseDetailParam>{

    @Override
    public void handler(TicketPurchaseDetailParam requestParam) {
        if (StrUtil.isBlank(requestParam.getTrainId())) {
            throw new ClientException("列车标识不能为空");
        }
        if (StrUtil.isBlank(requestParam.getDeparture())) {
            throw new ClientException("出发站点不能为空");
        }
        if (StrUtil.isBlank(requestParam.getArrival())) {
            throw new ClientException("到达站点不能为空");
        }
        if (CollUtil.isEmpty(requestParam.getPassengers())) {
            throw new ClientException("乘车人至少选择一位");
        }
        for (TicketPurchasePassengerDTO each : requestParam.getPassengers()) {
            if (StrUtil.isBlank(each.getPassengerId())) {
                throw new ClientException("乘车人不能为空");
            }
            if (Objects.isNull(each.getSeatType())) {
                throw new ClientException("座位类型不能为空");
            }
            if (CharUtil.isBlankChar(each.getChooseSeat())) {
                throw new ClientException("座位不能为空");
            }
        }
    }

    @Override
    public int getOrder() {
        return 1;
    }
}