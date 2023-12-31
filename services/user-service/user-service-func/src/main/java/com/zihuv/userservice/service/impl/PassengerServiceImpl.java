package com.zihuv.userservice.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zihuv.base.util.JSON;
import com.zihuv.cache.DistributedCache;
import com.zihuv.convention.exception.ServiceException;
import com.zihuv.index12306.frameworks.starter.user.core.UserContext;
import com.zihuv.userservice.common.constant.RedisKeyConstant;
import com.zihuv.userservice.common.enums.VerifyStatusEnum;
import com.zihuv.userservice.mapper.PassengerMapper;
import com.zihuv.userservice.model.entity.Passenger;
import com.zihuv.userservice.model.entity.UserPassenger;
import com.zihuv.userservice.model.param.PassengerParam;
import com.zihuv.userservice.service.PassengerService;
import com.zihuv.userservice.service.UserPassengerService;
import com.zihuv.userservice.model.vo.PassengerVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class PassengerServiceImpl extends ServiceImpl<PassengerMapper, Passenger> implements PassengerService {

    private final DistributedCache distributedCache;
    private final RedissonClient redissonClient;
    private final UserPassengerService userPassengerService;

    @Override
    public List<PassengerVO> listPassengerVO(Long userId) {
        List<Passenger> passengerList = getPassengerList(userId);
        // 封装 passengerVO 并返回
        return Objects.requireNonNull(passengerList)
                .stream()
                .map(passenger -> {
                    PassengerVO passengerVO = new PassengerVO();
                    passengerVO.setPassengerId(String.valueOf(passenger.getId()));
                    passengerVO.setRealName(passenger.getRealName());
                    passengerVO.setIdType(passenger.getIdType());
                    passengerVO.setIdCard(passenger.getIdCard());
                    passengerVO.setActualIdCard(passenger.getIdCard());
                    passengerVO.setDiscountType(passenger.getDiscountType());
                    passengerVO.setPhone(passenger.getPhone());
                    passengerVO.setActualPhone(passenger.getPhone());
                    passengerVO.setVerifyStatus(passenger.getVerifyStatus());
                    return passengerVO;
                })
                .toList();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void savePassenger(PassengerParam passengerParam) {

        // TODO 加锁，只允许一个线程去添加乘车人
        // 查询该用户是否已经将该乘车人添加进自己的乘车人当中
        List<Passenger> passengerList = getPassengerList(UserContext.getUserId());
        for (Passenger passenger : passengerList) {
            if (Objects.equals(passengerParam.getIdCard(), passenger.getIdCard())) {
                throw new ServiceException("该乘车人已经被您添加");
            }
        }
        // 封装 乘车人 并添加进数据库
        // 是否重复添加乘车人到数据库，交给数据库的身份证字段的 unique key 处理
        // TODO 使用布隆过滤器，查询缓存，查看是否已经存在该乘车人。否则出现无法解决数据库中的索引冲突异常
        Passenger passenger = new Passenger();
        passenger.setRealName(passengerParam.getRealName());
        passenger.setIdType(passengerParam.getIdType());
        passenger.setIdCard(passengerParam.getIdCard());
        passenger.setDiscountType(passengerParam.getDiscountType());
        passenger.setPhone(passengerParam.getPhone());
        passenger.setVerifyStatus(VerifyStatusEnum.REVIEWED.getCode());

        // 查询该乘坐人是否已经在数据库中存在。若存在，直接查询拿到 id；不存在，在数据库中添加后，拿到它的 id
        Long passengerId = -1L;
        LambdaQueryWrapper<Passenger> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Passenger::getIdCard, passenger.getIdCard());
        Passenger passenger1 = this.getOne(lqw);
        if (passenger1 == null) {
            // 加分布式锁，保证原子性
            RLock lock = redissonClient.getLock(RedisKeyConstant.PASSENGER_SAVE_LOCK + passenger.getIdCard());
            lock.lock();
            try {
                Passenger passenger2 = this.getOne(lqw);
                if (passenger2 == null) {
                    this.save(passenger);
                    passengerId = passenger.getId();
                } else {
                    passengerId = passenger2.getId();
                }
            } finally {
                lock.unlock();
            }
        } else {
            passengerId = passenger1.getId();
        }

        // 封装 用户乘车人中间表 并存入数据库
        Long userId = UserContext.getUserId();
        UserPassenger userPassenger = new UserPassenger();
        userPassenger.setUserId(userId);
        userPassenger.setPassengerId(passengerId);
        userPassengerService.save(userPassenger);
        // 删除该用户的乘车人缓存
        distributedCache.delete(RedisKeyConstant.USER_PASSENGER_LIST + userId);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updatePassenger(PassengerParam passengerParam) {
        Long passengerParamId = null;
        if (StrUtil.isEmpty(passengerParam.getPassengerId())) {
            throw new ServiceException("id不能为空");
        }
        if (StrUtil.isEmpty(passengerParam.getIdCard())) {
            throw new ServiceException("证件号码不能为空");
        }
        try {
            passengerParamId = Long.parseLong(passengerParam.getPassengerId());
        } catch (NumberFormatException e) {
            throw new ServiceException(StrUtil.format("请求id:[{}]不是纯数字或超过id最大值", passengerParam.getPassengerId()));
        }
        Passenger passenger = new Passenger();
        passenger.setRealName(passengerParam.getRealName());
        passenger.setIdType(passengerParam.getIdType());
        passenger.setIdCard(passengerParam.getIdCard());
        passenger.setDiscountType(passengerParam.getDiscountType());
        passenger.setPhone(passengerParam.getPhone());

        LambdaQueryWrapper<Passenger> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Passenger::getId, passengerParamId);
        this.update(passenger, lqw);
        distributedCache.delete(RedisKeyConstant.USER_PASSENGER_LIST + UserContext.getUserId());
    }

    @Override
    public void deletePassenger(String passengerId) {
        // 删除用户乘车人中间表，而不是乘车人
        LambdaQueryWrapper<UserPassenger> lqw = new LambdaQueryWrapper<>();
        lqw.eq(UserPassenger::getUserId, UserContext.getUserId());
        lqw.eq(UserPassenger::getPassengerId, passengerId);
        userPassengerService.remove(lqw);
        distributedCache.delete(RedisKeyConstant.USER_PASSENGER_LIST + UserContext.getUserId());
    }

    private List<Passenger> getPassengerList(Long userId) {
        // 查询 passenger json 集合字符串
        String passengerListJson = distributedCache.safeGet(RedisKeyConstant.USER_PASSENGER_LIST + userId, String.class, () -> {
            LambdaQueryWrapper<UserPassenger> lqw = new LambdaQueryWrapper<>();
            lqw.eq(UserPassenger::getUserId, userId);
            List<UserPassenger> userPassengerList = userPassengerService.list(lqw);
            List<Passenger> passengerList = new ArrayList<>();
            for (UserPassenger userPassenger : userPassengerList) {
                Passenger passenger = this.getById(userPassenger.getPassengerId());
                if (passenger != null) {
                    passengerList.add(passenger);
                }
            }
            return JSON.toJsonStr(passengerList);
        });
        // 将 json 转换成 passenger List 集合
        return JSON.toList(passengerListJson, Passenger.class);
    }

}