package com.zihuv.ticketservice.service.filter.query;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import com.zihuv.cache.DistributedCache;
import com.zihuv.ticketservice.common.constant.RedisKeyConstant;
import com.zihuv.ticketservice.model.entity.Region;
import com.zihuv.ticketservice.model.entity.Station;
import com.zihuv.ticketservice.model.param.TicketPageQueryParam;
import com.zihuv.ticketservice.service.RegionService;
import com.zihuv.ticketservice.service.StationService;
import com.zihuv.convention.exception.ClientException;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * 验证参数是否有效
 */
@Component
@RequiredArgsConstructor
public class TicketQueryCheckParamVerifyHandler implements TicketQueryChainFilter<TicketPageQueryParam> {

    private final RedisTemplate<String, Object> redisTemplate;
    private final RedissonClient redissonClient;
    private final DistributedCache distributedCache;
    private final RegionService regionService;
    private final StationService stationService;

    @Override
    public void handler(TicketPageQueryParam requestParam) {
        // 校验日期格式是否正确
        try {
            LocalDateTimeUtil.parse(requestParam.getDepartureDate(), DatePattern.NORM_DATE_PATTERN);
        } catch (Exception e) {
            throw new ClientException("日期格式错误");
        }
        // 验证日期是否有效
        LocalDateTime departureDate = LocalDateTimeUtil.parse(requestParam.getDepartureDate(), DatePattern.NORM_DATE_PATTERN);
        if (LocalDateTime.now().isAfter(departureDate.plusDays(1))) {
            throw new ClientException("出发日期不能小于当前日期");
        }
        // 查询出发地和目的地是否存在于缓存当中
        long emptyCacheCount = this.getEmptyCacheCount(requestParam);
        // 出发地和目的地均在缓存当中找到，不用再添加缓存，直接结束方法
        if (emptyCacheCount == 0L) {
            return;
        }

        // 加阻塞式锁，查找数据库重新加载缓存
        RLock lock = redissonClient.getLock(RedisKeyConstant.LOCK_QUERY_ALL_REGION_LIST);
        lock.lock();
        try {
            // 复查缓存，查看缓存中是否存在出发地和目的地
            if (distributedCache.hasKey(RedisKeyConstant.QUERY_ALL_REGION_LIST)) {
                emptyCacheCount = this.getEmptyCacheCount(requestParam);
                if (emptyCacheCount != 2L) {
                    throw new ClientException("出发地或目的地不存在");
                }
                // 出发地和目的地均在缓存中查询的到，结束方法
                return;
            }
            // 将所有地区和车站添加进缓存
            List<Region> regionList = regionService.list();
            List<Station> stationList = stationService.list();

            HashMap<Object, Object> regionValueMap = new HashMap<>();
            for (Region each : regionList) {
                regionValueMap.put(each.getCode(), each.getName());
            }
            for (Station each : stationList) {
                regionValueMap.put(each.getCode(), each.getName());
            }

            redisTemplate.opsForHash().putAll(RedisKeyConstant.QUERY_ALL_REGION_LIST, regionValueMap);
            // 复查缓存，查看缓存中是否存在出发地和目的地
            emptyCacheCount = this.getEmptyCacheCount(requestParam);
            if (emptyCacheCount != 2L) {
                throw new ClientException("出发地或目的地不存在");
            }
        } finally {
            lock.unlock();
        }
    }

    private long getEmptyCacheCount(TicketPageQueryParam requestParam) {
        List<Object> existList = redisTemplate.opsForHash().multiGet(
                RedisKeyConstant.QUERY_ALL_REGION_LIST,
                ListUtil.toList(requestParam.getDeparture(), requestParam.getArrival()));
        return existList.stream().filter(Objects::isNull).count();
    }

    @Override
    public int getOrder() {
        return 2;
    }
}