package com.example.datn_qlnt_manager.controller;

import com.example.datn_qlnt_manager.dto.ApiResponse;
import com.example.datn_qlnt_manager.dto.statistics.revenue.request.RevenueStatisticRequest;
import com.example.datn_qlnt_manager.dto.statistics.revenue.request.RevenueYearRequest;
import com.example.datn_qlnt_manager.dto.statistics.revenue.response.RevenueComparisonResponse;
import com.example.datn_qlnt_manager.dto.statistics.revenue.response.RevenueYearResponse;
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

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/revenues")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Revenue", description = "API Revenue")
public class RevenueStatisticController {
    RevenueStatisticService revenueStatisticService;

    @Operation(summary = "So sánh doanh thu từng tháng theo tòa nhà")
    @GetMapping("/month")
    public ApiResponse<List<RevenueComparisonResponse>> getMonthlyRevenueNextMonth(@Valid RevenueStatisticRequest request
    ) {

        return ApiResponse.<List<RevenueComparisonResponse>>builder()
                .message("Get revenue statistic successfully")
                .data(revenueStatisticService.getMonthlyRevenueNextMonthByBuilding(request))
                .build();
    }

    @Operation(summary = "Lấy doanh thu thực tế theo năm, tùy chọn theo tòa nhà")
    @GetMapping("/year")
    public ApiResponse<RevenueYearResponse> getRevenueByYear(
            RevenueYearRequest request
    ) {

        return ApiResponse.<RevenueYearResponse>builder()
                .message("Get revenue statistic successfully")
                .data(revenueStatisticService.getActualRevenueByYear(request))
                .build();
    }
}
