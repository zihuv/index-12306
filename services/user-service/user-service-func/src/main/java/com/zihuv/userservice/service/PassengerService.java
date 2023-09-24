package com.zihuv.userservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zihuv.userservice.model.entity.Passenger;
import com.zihuv.userservice.model.param.PassengerParam;
import com.zihuv.userservice.model.vo.PassengerVO;

import java.util.List;

public interface PassengerService extends IService<Passenger> {

    List<PassengerVO> listPassengerById(Long userId);

    void savePassenger(PassengerParam passengerParam);

    void updatePassenger(PassengerParam passengerParam);

    void deletePassenger(PassengerParam passengerParam);
}
