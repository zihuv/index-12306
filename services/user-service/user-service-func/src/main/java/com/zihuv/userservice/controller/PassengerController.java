package com.zihuv.userservice.controller;

import com.zihuv.convention.result.Result;
import com.zihuv.idempotent.annotation.Idempotent;
import com.zihuv.idempotent.enums.IdempotentSceneEnum;
import com.zihuv.idempotent.enums.IdempotentTypeEnum;
import com.zihuv.index12306.frameworks.starter.user.core.UserContext;
import com.zihuv.userservice.common.constant.IdempotentConstant;
import com.zihuv.userservice.feign.UserPassengerFeign;
import com.zihuv.userservice.model.param.PassengerParam;
import com.zihuv.userservice.model.vo.PassengerVO;
import com.zihuv.userservice.service.PassengerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "乘车人管理")
@Validated
@RestController
@RequiredArgsConstructor
public class PassengerController {

    private final PassengerService passengerService;

    /**
     * 根据 userId 查询其乘车人列表
     * #{@link UserPassengerFeign}
     */
    @Operation(summary = "根据 userId 查询其乘车人列表")
    @GetMapping("/api/user-service/passenger/query")
    public Result<List<PassengerVO>> listPassengerVO() {
        return Result.success(passengerService.listPassengerVO(UserContext.getUserId()));
    }

    /**
     * 新增该用户的乘车人列表
     */
    @Idempotent(
            uniqueKeyPrefix = IdempotentConstant.SAVE_PASSENGER_PREFIX,
            key = "T(com.zihuv.index12306.frameworks.starter.user.core.UserContext).getUsername()",
            type = IdempotentTypeEnum.SPEL,
            scene = IdempotentSceneEnum.RESTAPI,
            message = "正在新增乘车人，请稍后再试..."
    )
    @Operation(summary = "新增该用户的乘车人")
    @PostMapping("/api/user-service/passenger/save")
    public Result<?> savePassenger(@RequestBody PassengerParam passengerParam) {
        passengerService.savePassenger(passengerParam);
        return Result.success();
    }


    /**
     * 修改乘车人
     */
    @Idempotent(
            uniqueKeyPrefix = IdempotentConstant.UPDATE_PASSENGER_PREFIX,
            key = "T(com.zihuv.index12306.frameworks.starter.user.core.UserContext).getUsername()",
            type = IdempotentTypeEnum.SPEL,
            scene = IdempotentSceneEnum.RESTAPI,
            message = "正在修改乘车人，请稍后再试..."
    )
    @Operation(summary = "修改乘车人")
    @PostMapping("/api/user-service/passenger/update")
    public Result<?> updatePassenger(@RequestBody PassengerParam passengerParam) {
        passengerService.updatePassenger(passengerParam);
        return Result.success();
    }

    /**
     * 删除乘车人
     */
    @Operation(summary = "删除该用户的乘车人")
    @PostMapping("/api/user-service/passenger/delete")
    public Result<?> deletePassenger(String passengerId) {
        passengerService.deletePassenger(passengerId);
        return Result.success();
    }
}