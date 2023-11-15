package com.zihuv.ticketservice.feign;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = "index12306-ticket-service")
public interface TicketFeign {
}