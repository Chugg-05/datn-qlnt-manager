package com.example.datn_qlnt_manager.controller;

import com.example.datn_qlnt_manager.dto.ApiResponse;
import com.example.datn_qlnt_manager.dto.PaginatedResponse;
import com.example.datn_qlnt_manager.dto.filter.PaymentReceiptFilter;
import com.example.datn_qlnt_manager.dto.request.paymentReceipt.PaymentReceiptCreationRequest;
import com.example.datn_qlnt_manager.dto.request.paymentReceipt.PaymentReceiptStatusUpdateRequest;
import com.example.datn_qlnt_manager.dto.request.paymentReceipt.PaymentReceiptUpdateRequest;
import com.example.datn_qlnt_manager.dto.response.paymentReceipt.PaymentReceiptResponse;
import com.example.datn_qlnt_manager.service.PaymentReceiptService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payment-receipts")
@RequiredArgsConstructor
@Validated
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "PaymentReceipt", description = "API Payment Receipt")
public class PaymentReceiptController {

    PaymentReceiptService paymentReceiptService;

    @Operation(summary = "Tạo phiếu thanh toán")
    @PostMapping
    public ApiResponse<PaymentReceiptResponse> createPaymentReceipt(@Valid @RequestBody PaymentReceiptCreationRequest request){
        return ApiResponse.<PaymentReceiptResponse>builder()
                .data(paymentReceiptService.createPaymentReceipt(request))
                .message("Payment receipt created successfully")
                .build();
    }

    @Operation(summary = "Hiển thị, lọc và tìm kiếm phiếu thanh toán của user đang login")
    @GetMapping
    public ApiResponse<PaginatedResponse<PaymentReceiptResponse>> filterPaymentReceipts(
            @Valid @ModelAttribute PaymentReceiptFilter filter,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "15") int size) {
        return ApiResponse.<PaginatedResponse<PaymentReceiptResponse>>builder()
                .message("Show list of successful payment vouchers")
                .data(paymentReceiptService.filterPaymentReceiptsByUserId(filter, page, size))
                .build();
        }

    @Operation(summary = "Cập nhật phiếu thanh toán")
    @PutMapping("/{paymentReceiptId}")
    public ApiResponse<PaymentReceiptResponse> updatePaymentReceipt(
            @PathVariable String paymentReceiptId,
            @Valid @RequestBody PaymentReceiptUpdateRequest request) {
        return ApiResponse.<PaymentReceiptResponse>builder()
                .message("Payment receipt updated successfully")
                .data(paymentReceiptService.updatePaymentReceipt(paymentReceiptId, request))
                .build();
    }

    @Operation(summary = "Xóa phiếu thanh toán")
    @DeleteMapping("/{paymentReceiptId}")
    public ApiResponse<String> deletePaymentReceipt(@PathVariable String paymentReceiptId) {
        paymentReceiptService.deletePaymentReceipt(paymentReceiptId);
        return ApiResponse.<String>builder()
                .message("deleted payment receipt successfully")
                .build();
    }

    @Operation(summary = "Cập nhật trạng thái phiếu thanh toán")
    @PutMapping("/status/{paymentReceiptId}")
    public ApiResponse<PaymentReceiptResponse> updatePaymentReceiptStatus(
            @PathVariable String paymentReceiptId,
            @Valid @RequestBody PaymentReceiptStatusUpdateRequest request) {
        return ApiResponse.<PaymentReceiptResponse>builder()
                .message("Payment voucher status update successful")
                .data(paymentReceiptService.updatePaymentReceiptStatus(paymentReceiptId, request))
                .build();
    }

    @Operation(summary = "Xác nhận phiếu thanh toán")
    @PutMapping("/confirm/{paymentReceiptId}")
    public ApiResponse<PaymentReceiptResponse> confirmPaymentReceipt(
            @PathVariable String paymentReceiptId) {
        return ApiResponse.<PaymentReceiptResponse>builder()
                .message("Payment receipt confirmed successfully")
                .data(paymentReceiptService.confirmPaymentReceipt(paymentReceiptId))
                .build();
    }
    }
