package com.zihuv.ticketservice.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zihuv.ticketservice.model.entity.Station;
import com.zihuv.ticketservice.service.StationService;
import com.zihuv.ticketservice.mapper.StationMapper;
import org.springframework.stereotype.Service;

@Service
public class StationServiceImpl extends ServiceImpl<StationMapper, Station> implements StationService{

}




