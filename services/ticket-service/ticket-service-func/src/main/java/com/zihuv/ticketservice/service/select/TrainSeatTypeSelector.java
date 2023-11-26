package com.zihuv.ticketservice.service.select;

import cn.hutool.core.util.StrUtil;
import com.zihuv.convention.exception.ServiceException;
import com.zihuv.index12306.frameworks.starter.user.core.UserContext;
import com.zihuv.ticketservice.common.constant.RedisKeyConstant;
import com.zihuv.ticketservice.model.dto.RouteDTO;
import com.zihuv.ticketservice.model.dto.SeatChooseDTO;
import com.zihuv.ticketservice.model.param.TicketPurchaseDetailParam;
import com.zihuv.ticketservice.service.TrainStationService;
import com.zihuv.userservice.feign.UserPassengerFeign;
import com.zihuv.ticketservice.model.dto.TicketPurchaseDTO;
import com.zihuv.ticketservice.model.dto.TicketPurchasePassengerDTO;
import com.zihuv.ticketservice.model.entity.Seat;
import com.zihuv.ticketservice.service.SeatService;
import com.zihuv.ticketservice.service.TrainService;
import com.zihuv.userservice.pojo.PassengerVO;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 购票时列车座位选择器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public final class TrainSeatTypeSelector {

    private final RedisTemplate<String, Object> redisTemplate;
    private final SeatService seatService;
    private final TrainService trainService;
    private final TrainStationService trainStationService;
    private final UserPassengerFeign userPassengerFeign;

    public List<TicketPurchaseDTO> select(TicketPurchaseDetailParam requestParam) {
        // TODO 提高选座速度方案：1.加缓存 2.不加锁，使用 lua 脚本，在内存中计算，之后异步写入 db

        List<TicketPurchasePassengerDTO> passengers = requestParam.getPassengers();

        // 查询该乘客从出发地到目的地的路线
        List<RouteDTO> routeDTOList = trainStationService.listTrainStationRoute(requestParam.getTrainId(), requestParam.getDeparture(), requestParam.getArrival());
        List<SeatChooseDTO> seatChooseDTOList = new ArrayList<>();

        // 根据座位类型和选择的座位位置，查看是否有合适的位置
        // TODO 在某一列座位没有了后，在该座位类型中随机分配一个座位给乘客
        boolean successChooseSeat = false;
        int routeCount = 0;
        Object chooseSeat = null;
        for (TicketPurchasePassengerDTO passenger : passengers) {
            List<String> seatCarriageList = seatService.listSeatCarriageByTrainIdAndSeatType(requestParam.getTrainId(), passenger.getSeatType());
            for (String seatCarriage : seatCarriageList) {
                boolean firstChooseSeat = true;
                for (RouteDTO routeDTO : routeDTOList) {
                    String route = routeDTO.getStartStation() + "_" + routeDTO.getEndStation();
                    // 列车id:{}:路线:{}:车厢:{}:座位字母:{}-座位
                    String seatKey = StrUtil.format(RedisKeyConstant.SEAT_BUCKET, requestParam.getTrainId(), route, seatCarriage, passenger.getChooseSeat());
                    // 校验这个作为是否存在
                    boolean seatIsExist = !Objects.equals(redisTemplate.opsForSet().isMember(seatKey, passenger.getChooseSeat()), Boolean.TRUE);
                    if (seatIsExist) {
                        if (firstChooseSeat) {
                            chooseSeat = redisTemplate.opsForSet().pop(seatKey);
                            if (chooseSeat == null) {
                                break;
                            }
                            firstChooseSeat = false;
                        } else {
                            redisTemplate.opsForSet().remove(seatKey, chooseSeat);
                        }

                        SeatChooseDTO seatChooseDTO = new SeatChooseDTO();
                        seatChooseDTO.setPassengerId(passenger.getPassengerId());
                        seatChooseDTO.setCarriageNumber(seatCarriage);
                        seatChooseDTO.setSeatNumber(String.valueOf(chooseSeat));
                        // TODO 封装价格 seatChooseDTO.setPrice();
                        seatChooseDTOList.add(seatChooseDTO);

                        routeCount++;
                        if (routeCount >= routeDTOList.size()) {
                            successChooseSeat = true;
                        }
                    }
                    if (successChooseSeat) {
                        break;
                    }
                }
                if (successChooseSeat) {
                    break;
                }
            }
        }

        List<PassengerVO> userAllPassenger = userPassengerFeign.listPassengerVO(String.valueOf(UserContext.getUserId())).getData();
        if (userAllPassenger == null) {
            throw new ServiceException("该用户并没有选择乘坐人");
        }
        List<TicketPurchaseDTO> ticketPurchaseDTOList = new ArrayList<>();
        for (TicketPurchasePassengerDTO ticketPurchasePassengerDTO : passengers) {
            Optional<PassengerVO> passengerOptional = userAllPassenger.stream().filter((each) -> each.getPassengerId().equals(ticketPurchasePassengerDTO.getPassengerId())).findFirst();
            if (passengerOptional.isEmpty()) {
                throw new ServiceException("该乘坐人不存在");
            }
            Optional<SeatChooseDTO> seatChooseOptional = seatChooseDTOList.stream().filter((each) -> each.getPassengerId().equals(ticketPurchasePassengerDTO.getPassengerId())).findFirst();
            if (seatChooseOptional.isEmpty()) {
                throw new ServiceException("所选座位不存在");
            }
            PassengerVO passenger = passengerOptional.get();
            SeatChooseDTO seatChooseDTO = seatChooseOptional.get();

            TicketPurchaseDTO ticketPurchaseDTO = new TicketPurchaseDTO();
            ticketPurchaseDTO.setPassengerId(ticketPurchasePassengerDTO.getPassengerId());
            ticketPurchaseDTO.setRealName(passenger.getRealName());
            ticketPurchaseDTO.setIdType(passenger.getIdType());
            ticketPurchaseDTO.setIdCard(passenger.getIdCard());
            ticketPurchaseDTO.setPhone(passenger.getPhone());
            ticketPurchaseDTO.setUserType(passenger.getDiscountType());
            ticketPurchaseDTO.setSeatType(ticketPurchasePassengerDTO.getSeatType());
            ticketPurchaseDTO.setCarriageNumber(seatChooseDTO.getCarriageNumber());
            ticketPurchaseDTO.setSeatNumber(seatChooseDTO.getSeatNumber());
            ticketPurchaseDTO.setPrice(seatChooseDTO.getPrice());
            ticketPurchaseDTOList.add(ticketPurchaseDTO);
        }
        return ticketPurchaseDTOList;
    }

    /**
     * 初始化座位占用缓存
     * TODO 存在问题：令牌桶和实际空余座位不一致
     */
    @PostConstruct
    public void initSeatBucketHashKey() {
        // 查询所有列车 id
        List<Long> trainIds = trainService.listTrainIds();
        for (Long trainId : trainIds) {
            List<Seat> seatList = seatService.listSeatAllByTrainId(String.valueOf(trainId));
            for (Seat seat : seatList) {
                // 列车id:{}:路线:{}:车厢:{}:座位字母:{}-座位
                String route = seat.getStartStation() + "_" + seat.getEndStation();
                String seatKey = StrUtil.format(RedisKeyConstant.SEAT_BUCKET, trainId, route, seat.getCarriageNumber(), seat.getSeatLetter());
                redisTemplate.opsForSet().add(seatKey, seat.getSeatNumber());

            }
        }
    }
}