package com.zihuv.ticketservice.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zihuv.ticketservice.model.entity.Train;
import com.zihuv.ticketservice.service.TrainService;
import com.zihuv.ticketservice.mapper.TrainMapper;
import org.springframework.stereotype.Service;

@Service
public class TrainServiceImpl extends ServiceImpl<TrainMapper, Train> implements TrainService{

}




