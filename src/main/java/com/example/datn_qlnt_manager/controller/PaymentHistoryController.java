package com.example.datn_qlnt_manager.controller;

import com.example.datn_qlnt_manager.dto.ApiResponse;
import com.example.datn_qlnt_manager.dto.PaginatedResponse;
import com.example.datn_qlnt_manager.dto.filter.PaymentHistoryFilter;


import com.example.datn_qlnt_manager.dto.response.paymenthistory.PaymentHistoryResponse;
import com.example.datn_qlnt_manager.service.PaymentHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payment-histories")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentHistoryController {
    PaymentHistoryService paymentHistoryService;

    @Operation(summary = "Hiển thị, lọc và tìm kiếm lịch sử thanh toán của user đang login")
    @GetMapping
    public ApiResponse<PaginatedResponse<PaymentHistoryResponse>> filterPaymentHistories(
            @Valid @ModelAttribute PaymentHistoryFilter filter,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "15") int size) {
        return ApiResponse.<PaginatedResponse<PaymentHistoryResponse>>builder()
                .message("Show list of payment histories")
                .data(paymentHistoryService.filterPaymentHistoriesByUserId(filter, page, size))
                .build();
    }

    @Operation(summary = "Khách thuê xem lịch sử thanh toán của họ")
    @GetMapping("/my-payment-history")
    public ApiResponse<PaginatedResponse<PaymentHistoryResponse>> filterMyPaymentHistories(
            @Valid @ModelAttribute PaymentHistoryFilter filter,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "15") int size) {
        return ApiResponse.<PaginatedResponse<PaymentHistoryResponse>>builder()
                .message("Show my payment histories")
                .data(paymentHistoryService.filterMyPaymentHistories(filter, page, size))
                .build();
    }
}
