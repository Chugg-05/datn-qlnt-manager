package com.example.datn_qlnt_manager.controller;

import com.example.datn_qlnt_manager.dto.ApiResponse;
import com.example.datn_qlnt_manager.dto.PaginatedResponse;
import com.example.datn_qlnt_manager.dto.filter.ServicePriceHistoryFilter;
import com.example.datn_qlnt_manager.dto.response.servicePriceHistory.ServicePriceHistoryResponse;
import com.example.datn_qlnt_manager.service.ServicePriceHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/service-price-histories")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "ServicePriceHistory", description = "API Service Price History")

public class ServicePriceHistoryController {

    ServicePriceHistoryService servicePriceHistoryService;

    @Operation(summary = "Phân trang, lọc lịch sử giá theo dịch vụ, giá cũ/mới, ngày áp dụng")
    @GetMapping
    public ApiResponse<PaginatedResponse<ServicePriceHistoryResponse>> getServicePriceHistories(
            @Valid @ModelAttribute ServicePriceHistoryFilter filter,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "15") int size
    ) {
        return ApiResponse.<PaginatedResponse<ServicePriceHistoryResponse>>builder()
                .data(servicePriceHistoryService.getServicePriceHistories(page, size, filter))
                .message("Get service price history successfully")
                .build();
    }
}
