package com.zihuv.ticketservice.tokenbucket;

import cn.hutool.core.lang.Singleton;
import cn.hutool.core.util.StrUtil;
import com.zihuv.DistributedCache;
import com.zihuv.base.util.JSON;
import com.zihuv.ticketservice.common.constant.Index12306Constant;
import com.zihuv.ticketservice.common.enums.VehicleTypeEnum;
import com.zihuv.ticketservice.model.dto.PurchaseTicketPassengerDTO;
import com.zihuv.ticketservice.model.dto.RouteDTO;
import com.zihuv.ticketservice.model.dto.SeatTypeCountDTO;
import com.zihuv.ticketservice.model.entity.Train;
import com.zihuv.ticketservice.model.param.PurchaseTicketDetailParam;
import com.zihuv.ticketservice.service.SeatService;
import com.zihuv.ticketservice.service.TrainService;
import com.zihuv.ticketservice.service.TrainStationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.zihuv.ticketservice.common.constant.RedisKeyConstant.*;

/**
 * 列车车票余量令牌桶，应对海量并发场景下满足并行、限流以及防超卖等场景
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TicketAvailabilityTokenBucket {

    private final DistributedCache distributedCache;
    private final RedissonClient redissonClient;
    private final RedisTemplate<String, Object> redisTemplate;
    private final TrainService trainService;
    private final TrainStationService trainStationService;
    private final SeatService seatService;

    //private static final String LUA_TICKET_AVAILABILITY_TOKEN_BUCKET_PATH = "lua/ticket_availability_token_bucket.lua";
    private static final String LUA_TICKET_AVAILABILITY_TOKEN_BUCKET_PATH = "lua/ticket_availability_token_bucket.lua";

    public boolean takeTokenFromBucket(PurchaseTicketDetailParam purchaseTicketDetail) {
        // 查询列车
        Train train = distributedCache.safeGet(
                TRAIN_INFO + purchaseTicketDetail.getTrainId(),
                Train.class,
                () -> trainService.getById(purchaseTicketDetail.getTrainId()),
                Index12306Constant.ADVANCE_TICKET_DAY,
                TimeUnit.DAYS);
        // 获取该列车所有经过的路线
        List<RouteDTO> allRouteList = trainStationService.listTrainStationRoute(String.valueOf(train.getId()));
        // 查询是否存在令牌桶（用于存储该列车各座位类型的票数）
        String tokenBucketHashKey = TICKET_AVAILABILITY_TOKEN_BUCKET + train.getId();
        Boolean hasKey1 = distributedCache.hasKey(tokenBucketHashKey);
        // 不存在令牌桶，加阻塞式分布式锁创建
        if (!hasKey1) {
            RLock lock = redissonClient.getLock(String.format(LOCK_TICKET_AVAILABILITY_TOKEN_BUCKET, train.getId()));
            lock.lock();
            try {
                // 再次查看是否存在令牌桶
                Boolean hasKey2 = distributedCache.hasKey(tokenBucketHashKey);
                if (!hasKey2) {
                    // 获取该列车的座位类型（eg：商务座-0、一等座-1、二等座-2）
                    List<Integer> seatTypes = VehicleTypeEnum.findSeatTypesByCode(train.getTrainType());
                    // <路线_座位类型, 票数>
                    Map<String, Integer> ticketAvailabilityTokenMap = new HashMap<>();
                    for (RouteDTO routeDTO : allRouteList) {
                        // 获取某个路线中的座位类型各售多少票数
                        List<SeatTypeCountDTO> seatTypeCountList = seatService.listSeatTypeCount(train.getId(), routeDTO.getStartStation(), routeDTO.getEndStation(), seatTypes);
                        // 构建令牌桶
                        for (SeatTypeCountDTO seatTypeCountDTO : seatTypeCountList) {
                            String buildCacheKey = StrUtil.join("_", routeDTO.getStartStation(), routeDTO.getEndStation(), seatTypeCountDTO.getSeatType());
                            ticketAvailabilityTokenMap.put(buildCacheKey, seatTypeCountDTO.getSeatCount());
                        }
                    }
                    redisTemplate.opsForHash().putAll(TICKET_AVAILABILITY_TOKEN_BUCKET + train.getId(), ticketAvailabilityTokenMap);
                }
            } finally {
                lock.unlock();
            }
        }
        // 获取乘坐人购买的座位票数，确定需要领取的令牌数 eg：二等座，两张票
        List<SeatTypeCountDTO> seatTypeCountList = purchaseTicketDetail.getPassengers().stream()
                .collect(Collectors.groupingBy(PurchaseTicketPassengerDTO::getSeatType, Collectors.counting())).entrySet().stream()
                .map(entry -> new SeatTypeCountDTO(entry.getKey(), Math.toIntExact(entry.getValue())))
                .toList();
        // 乘客所购买的路段
        List<RouteDTO> passengerRouteList = trainStationService.listTrainStationRoute(String.valueOf(train.getId()), purchaseTicketDetail.getDeparture(), purchaseTicketDetail.getArrival());
        // 令牌桶映射，记录买哪段路线座位的票，票数 eg：<北京_德州_0，两张票>
        Map<String, Long> bucketMap = new HashMap<>();
        for (RouteDTO routeDTO : passengerRouteList) {
            for (SeatTypeCountDTO seatTypeCountDTO : seatTypeCountList) {
                String routeAndSeatKey = StrUtil.join("_", routeDTO.getStartStation(), routeDTO.getEndStation(), seatTypeCountDTO.getSeatType());
                bucketMap.put(routeAndSeatKey, bucketMap.getOrDefault(routeAndSeatKey, 0L) + 1);
            }
        }
        // 使用 lua 脚本，查看并减去令牌桶中的令牌
        DefaultRedisScript<Long> script = Singleton.get(LUA_TICKET_AVAILABILITY_TOKEN_BUCKET_PATH, () -> {
            DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
            redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource(LUA_TICKET_AVAILABILITY_TOKEN_BUCKET_PATH)));
            redisScript.setResultType(Long.class);
            return redisScript;
        });
        Long result = redisTemplate.execute(script, List.of(tokenBucketHashKey, JSON.toJsonStr(bucketMap)),new ArrayList<>());
        // 若令牌数量充足，lua 脚本将返回 1，代表成功拿到令牌，允许购票
        return result != null && result == 1L;
    }
}