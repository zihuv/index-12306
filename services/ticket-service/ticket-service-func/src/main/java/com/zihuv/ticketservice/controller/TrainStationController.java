package com.zihuv.ticketservice.controller;

import com.zihuv.convention.result.Result;
import com.zihuv.ticketservice.model.vo.TrainStationVO;
import com.zihuv.ticketservice.service.TrainStationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "列车站点管理")
@Validated
@RestController
@RequiredArgsConstructor
public class TrainStationController {

    private final TrainStationService trainStationService;

    /**
     * 根据列车 id 查询站点信息
     */
    @Operation(summary = "根据列车 id 查询站点信息")
    @GetMapping("/api/ticket-service/train-station/query")
    public Result<List<TrainStationVO>> listTrainStationQuery(String trainId) {
        return Result.success(trainStationService.listTrainStationVO(trainId));
    }
}