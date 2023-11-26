package com.zihuv.ticketservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zihuv.ticketservice.mapper.TrainMapper;
import com.zihuv.ticketservice.model.entity.Train;
import com.zihuv.ticketservice.service.TrainService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TrainServiceImpl extends ServiceImpl<TrainMapper, Train> implements TrainService {

    @Override
    public List<Long> listTrainIds() {
        LambdaQueryWrapper<Train> lqw = new LambdaQueryWrapper<>();
        lqw.select(Train::getId);
        List<Train> trainList = this.list(lqw);
        List<Long> trainIds = new ArrayList<>();
        for (Train train : trainList) {
            trainIds.add(train.getId());
        }
        return trainIds;
    }
}




