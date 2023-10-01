package com.zihuv.ticketservice.controller;

import com.zihuv.convention.result.Result;
import com.zihuv.ticketservice.model.vo.StationVO;
import com.zihuv.ticketservice.service.RegionStationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "车站管理")
@Validated
@RestController
@RequiredArgsConstructor
public class RegionStationController {

    private final RegionStationService regionStationService;

    /**
     * 查询车站站点集合信息
     */
    @Operation(summary = "查询车站站点集合信息")
    @GetMapping("/api/ticket-service/station/all")
    public Result<List<StationVO>> listAllStation() {
        return Result.success(regionStationService.listAllStation());
    }


}