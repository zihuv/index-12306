package com.zihuv.ticketservice.service;

import com.zihuv.ticketservice.model.entity.Ticket;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zihuv.ticketservice.model.param.TicketPurchaseDetailParam;
import com.zihuv.ticketservice.model.param.TicketPageQueryParam;
import com.zihuv.ticketservice.model.vo.TicketPurchaseVO;
import com.zihuv.ticketservice.model.vo.TicketPageQueryVO;

import java.util.List;

public interface TicketService extends IService<Ticket> {

    List<TicketPageQueryVO> pageTicketQuery(TicketPageQueryParam ticketPageQueryParam);

    TicketPurchaseVO purchaseTickets(TicketPurchaseDetailParam purchaseTicket);
}
