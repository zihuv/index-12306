package com.zihuv.ticketservice.service;

import com.zihuv.ticketservice.model.entity.Train;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface TrainService extends IService<Train> {

    /***
     * 查询所有列车的 id
     */
    List<Long> listTrainIds();
}
