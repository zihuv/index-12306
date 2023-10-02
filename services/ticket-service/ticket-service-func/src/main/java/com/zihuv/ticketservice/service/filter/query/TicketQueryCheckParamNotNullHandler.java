package com.zihuv.ticketservice.service.filter.query;

import cn.hutool.core.util.StrUtil;
import com.zihuv.convention.exception.ClientException;
import com.zihuv.ticketservice.model.param.TicketPageQueryParam;
import org.springframework.stereotype.Component;

/**
 * 校验参数不能为空
 */
@Component
public class TicketQueryCheckParamNotNullHandler implements TicketQueryChainFilter<TicketPageQueryParam> {
    @Override
    public void handler(TicketPageQueryParam requestParam) {
        if (StrUtil.isBlank(requestParam.getFromStationCode())) {
            throw new ClientException("出发地不能为空");
        }
        if (StrUtil.isBlank(requestParam.getToStationCode())) {
            throw new ClientException("目的地不能为空");
        }
        if (StrUtil.isBlank(requestParam.getDepartureDate())) {
            throw new ClientException("出发日期不能为空");
        }
    }

    @Override
    public int getOrder() {
        return 1;
    }
}