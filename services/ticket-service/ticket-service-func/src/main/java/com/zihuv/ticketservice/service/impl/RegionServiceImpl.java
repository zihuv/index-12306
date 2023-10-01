package com.zihuv.ticketservice.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zihuv.ticketservice.model.entity.Region;
import com.zihuv.ticketservice.service.RegionService;
import com.zihuv.ticketservice.mapper.RegionMapper;
import org.springframework.stereotype.Service;

@Service
public class RegionServiceImpl extends ServiceImpl<RegionMapper, Region> implements RegionService{

}




