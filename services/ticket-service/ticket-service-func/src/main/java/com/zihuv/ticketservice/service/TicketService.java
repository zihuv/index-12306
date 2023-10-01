package com.zihuv.ticketservice.service;

import com.zihuv.ticketservice.model.entity.Ticket;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zihuv.ticketservice.model.param.PurchaseTicketDetailParam;
import com.zihuv.ticketservice.model.vo.TicketOrderDetailVO;

public interface TicketService extends IService<Ticket> {

    TicketOrderDetailVO purchaseTickets(PurchaseTicketDetailParam purchaseTicket);
}
