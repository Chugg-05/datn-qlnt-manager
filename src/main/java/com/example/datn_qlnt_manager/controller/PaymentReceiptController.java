package com.example.datn_qlnt_manager.controller;

import com.example.datn_qlnt_manager.dto.ApiResponse;
import com.example.datn_qlnt_manager.dto.PaginatedResponse;
import com.example.datn_qlnt_manager.dto.filter.PaymentReceiptFilter;
import com.example.datn_qlnt_manager.dto.request.paymentReceipt.*;
import com.example.datn_qlnt_manager.dto.response.paymentReceipt.PaymentBatchResponse;
import com.example.datn_qlnt_manager.dto.response.paymentReceipt.PaymentMethodResponse;
import com.example.datn_qlnt_manager.dto.response.paymentReceipt.PaymentReceiptResponse;
import com.example.datn_qlnt_manager.dto.response.paymentReceipt.RejectPaymentResponse;
import com.example.datn_qlnt_manager.service.PaymentReceiptService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;

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
    public ApiResponse<PaymentReceiptResponse> createPaymentReceipt(@Valid @RequestBody PaymentReceiptCreationRequest request) {
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

    @Operation(summary = "Hiển thị, lọc và tìm kiếm phiếu thanh toán của khách thuê đang login")
    @GetMapping("/by-tenant")
    public ApiResponse<PaginatedResponse<PaymentReceiptResponse>> findPaymentReceiptsByTenant(
            @Valid @ModelAttribute PaymentReceiptFilter filter,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "15") int size) {
        return ApiResponse.<PaginatedResponse<PaymentReceiptResponse>>builder()
                .message("Get a list of successful tenant payments")
                .data(paymentReceiptService.filterPaymentReceiptsByTenantId(filter, page, size))
                .build();
    }


    @Operation(summary = "Hiển thị phiếu thanh toán của khách thuê đang login")
    @GetMapping("/{invoiceId}")
    public ApiResponse<PaymentReceiptResponse> findPaymentReceiptByInvoiceId(@PathVariable String invoiceId) {
        return ApiResponse.<PaymentReceiptResponse>builder()
                .message("success")
                .data(paymentReceiptService.findPaymentReceiptByInvoiceId(invoiceId))
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


    @Operation(summary = "Gửi thông báo thanh toán hóa đơn tháng tới khách hàng")
    @PostMapping("/send-payment-notice")
    public ApiResponse<PaymentBatchResponse> generatePaymentReceiptsForCurrentMonth() {
        PaymentBatchResponse response = paymentReceiptService.generateMonthlyPaymentRequests();
        return ApiResponse.<PaymentBatchResponse>builder()
                .message("Create and send payment receipt successfully")
                .data(response)
                .build();
    }

    @Operation(summary = "Xác nhận phương thức thanh toán từ người khách thuê")
    @PatchMapping("/confirm/{receiptId}")
    public ApiResponse<PaymentMethodResponse> confirmPaymentMethod(
            @PathVariable("receiptId") String receiptId,
            @RequestBody PaymentMethodRequest request
    ) {
        return ApiResponse.<PaymentMethodResponse>builder()
                .message("Confirm payment method successfully")
                .data(paymentReceiptService.confirmPaymentMethod(receiptId, request))
                .build();
    }

    @Operation(summary = "Từ chối thanh toán")
    @PatchMapping("/reject/{receiptId}")
    public ApiResponse<RejectPaymentResponse> rejectPaymentReceipt(
            @PathVariable("receiptId") String receiptId,
            @RequestBody @Valid RejectPaymentRequest request
    ) {
        return ApiResponse.<RejectPaymentResponse>builder()
                .message("Reject payment receipt successfully")
                .data(paymentReceiptService.rejectPaymentReceipt(receiptId, request))
                .build();
    }

    @Operation(summary = "Xác nhận đã thanh toán toán cho phiếu thanh toán bằng tiền mặt")
    @PatchMapping("/payment-confirm/{receiptId}")
    public ApiResponse<String> confirmCashPayment(@PathVariable("receiptId") String receiptId) {
        paymentReceiptService.confirmCashPayment(receiptId);

        return ApiResponse.<String>builder()
                .message("Confirm cash payment successfully")
                .data("Confirmation of paid " + receiptId + " invoice")
                .build();
    }

    @Operation(summary = "Tạo đường dẫn tới thanh toán bằng VNPAY")
    @PostMapping("/create-payment-url")
    public ApiResponse<String> createPaymentUrl(@Valid @RequestBody PaymentCreationURL paymentCreationURL,
                                                HttpServletRequest httpServletRequest) {
        return ApiResponse.<String>builder()
                .message("Payment creation url successfully")
                .data(paymentReceiptService.createPaymentUrl(paymentCreationURL, httpServletRequest))
                .build();
    }
}
