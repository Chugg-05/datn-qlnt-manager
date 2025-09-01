package com.example.datn_qlnt_manager.controller;

import com.example.datn_qlnt_manager.dto.ApiResponse;
import com.example.datn_qlnt_manager.dto.statistics.revenue.request.RevenueStatisticRequest;
import com.example.datn_qlnt_manager.dto.statistics.revenue.response.RevenueComparisonResponse;
import com.example.datn_qlnt_manager.service.RevenueStatisticService;
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

    @GetMapping("/comparison")
    public ApiResponse<List<RevenueComparisonResponse>> getRevenueComparison(@Valid RevenueStatisticRequest request
    ) {
        List<RevenueComparisonResponse> responses =
                revenueStatisticService.compareRevenueByBuilding(request);

        return ApiResponse.<List<RevenueComparisonResponse>>builder()
                .message("Get revenue comparison successfully")
                .data(responses)
                .build();
    }
}
