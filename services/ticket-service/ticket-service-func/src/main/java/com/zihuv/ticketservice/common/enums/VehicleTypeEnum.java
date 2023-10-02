package com.zihuv.ticketservice.common.enums;

import cn.hutool.core.collection.ListUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.zihuv.ticketservice.common.enums.VehicleSeatTypeEnum.*;

/**
 * 交通工具类型
 */
@Getter
@RequiredArgsConstructor
public enum VehicleTypeEnum {

    /**
     * 高铁
     */
    HIGH_SPEED_RAIN(0, "HIGH_SPEED_RAIN", "高铁", ListUtil.of(BUSINESS_CLASS.getCode(), FIRST_CLASS.getCode(), SECOND_CLASS.getCode())),

    /**
     * 动车
     */
    BULLET(1, "BULLET", "动车", ListUtil.of(SECOND_CLASS_CABIN_SEAT.getCode(), FIRST_SLEEPER.getCode(), SECOND_SLEEPER.getCode(), NO_SEAT_SLEEPER.getCode())),

    /**
     * 普通车
     */
    REGULAR_TRAIN(2, "REGULAR_TRAIN", "普通车", ListUtil.of(SOFT_SLEEPER.getCode(), HARD_SLEEPER.getCode(), HARD_SEAT.getCode(), NO_SEAT_SLEEPER.getCode())),

    /**
     * 汽车
     */
    CAR(3, "CAR", "汽车", null),

    /**
     * 飞机
     */
    AIRPLANE(4, "AIRPLANE", "飞机", null);


    private final Integer code;

    private final String name;

    private final String value;

    private final List<Integer> seatTypes;

    /**
     * 根据编码查找座位类型集合
     */
    public static List<Integer> findSeatTypesByCode(Integer code) {
        return Arrays.stream(VehicleTypeEnum.values())
                .filter(each -> Objects.equals(each.getCode(), code))
                .findFirst()
                .map(VehicleTypeEnum::getSeatTypes)
                .orElse(null);
    }
}