package com.example.datn_qlnt_manager.controller;

import com.example.datn_qlnt_manager.dto.ApiResponse;
import com.example.datn_qlnt_manager.dto.statistics.revenue.RevenueStatisticRequest;
import com.example.datn_qlnt_manager.dto.statistics.revenue.RevenueStatisticResponse;
import com.example.datn_qlnt_manager.service.RevenueStatisticService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/revenues")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Revenue", description = "API Revenue")
public class RevenueStatisticController {
    RevenueStatisticService revenueStatisticService;

    @Operation(summary = "Thống kê doanh thu")
    @GetMapping("/statistic")
    public ApiResponse<RevenueStatisticResponse> getRevenueStatistic(@Valid RevenueStatisticRequest request) {
        RevenueStatisticResponse response = revenueStatisticService.getRevenueStatistic(request);

        return ApiResponse.<RevenueStatisticResponse>builder()
                .message("Get revenue statistic successfully")
                .data(response)
                .build();
    }
}
