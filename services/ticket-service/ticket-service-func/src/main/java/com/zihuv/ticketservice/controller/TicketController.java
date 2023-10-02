package com.zihuv.ticketservice.controller;

import com.zihuv.convention.result.Result;
import com.zihuv.idempotent.annotation.Idempotent;
import com.zihuv.idempotent.enums.IdempotentSceneEnum;
import com.zihuv.idempotent.enums.IdempotentTypeEnum;
import com.zihuv.log.annotation.ILog;
import com.zihuv.ticketservice.common.constant.IdempotentConstant;
import com.zihuv.ticketservice.model.param.PurchaseTicketDetailParam;
import com.zihuv.ticketservice.model.param.TicketPageQueryParam;
import com.zihuv.ticketservice.model.vo.TicketOrderDetailVO;
import com.zihuv.ticketservice.model.vo.TicketPageQueryVO;
import com.zihuv.ticketservice.service.TicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.zihuv.index12306.frameworks.starter.user.constant.UserContextConstant.USER_CONTEXT_SPEL;

@Tag(name = "车票管理")
@Validated
@RestController
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    /**
     * 查询车票
     */
    @Operation(summary = "查询车票")
    @PostMapping("/api/ticket-service/ticket/query")
    public Result<List<TicketPageQueryVO>> pageListTicketQuery(@RequestBody TicketPageQueryParam ticketPageQueryParam) {
        return Result.success(ticketService.pageTicketQuery(ticketPageQueryParam));
    }

    /**
     * 购买车票
     */
    @ILog
    @Idempotent(
            uniqueKeyPrefix = IdempotentConstant.PURCHASE_TICKETS,
            key = USER_CONTEXT_SPEL,
            message = "正在执行下单流程，请稍后...",
            scene = IdempotentSceneEnum.RESTAPI,
            type = IdempotentTypeEnum.SPEL
    )
    @Operation(summary = "购买车票")
    @PostMapping("/api/ticket-service/ticket/purchase")
    public Result<TicketOrderDetailVO> purchaseTickets(@RequestBody PurchaseTicketDetailParam purchaseTicket) {
        return Result.success(ticketService.purchaseTickets(purchaseTicket));
    }
}