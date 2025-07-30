package com.example.datn_qlnt_manager.controller;

import java.util.List;

import com.example.datn_qlnt_manager.dto.request.paymentReceipt.PaymentMethodRequest;
import com.example.datn_qlnt_manager.dto.request.paymentReceipt.RejectPaymentRequest;
import com.example.datn_qlnt_manager.dto.response.paymentReceipt.PaymentBatchResponse;
import com.example.datn_qlnt_manager.dto.response.paymentReceipt.PaymentMethodResponse;
import com.example.datn_qlnt_manager.dto.response.paymentReceipt.RejectPaymentResponse;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;

import com.example.datn_qlnt_manager.dto.ApiResponse;
import com.example.datn_qlnt_manager.dto.PaginatedResponse;
import com.example.datn_qlnt_manager.dto.filter.InvoiceFilter;
import com.example.datn_qlnt_manager.dto.request.invoice.InvoiceBuildingCreationRequest;
import com.example.datn_qlnt_manager.dto.request.invoice.InvoiceCreationRequest;
import com.example.datn_qlnt_manager.dto.request.invoice.InvoiceFloorCreationRequest;
import com.example.datn_qlnt_manager.dto.request.invoice.InvoiceUpdateRequest;
import com.example.datn_qlnt_manager.dto.response.invoice.InvoiceDetailsResponse;
import com.example.datn_qlnt_manager.dto.response.invoice.InvoiceResponse;
import com.example.datn_qlnt_manager.dto.statistics.InvoiceStatistics;
import com.example.datn_qlnt_manager.service.InvoiceService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/invoices")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Invoice", description = "API invoice")
public class InvoiceController {
    InvoiceService invoiceService;

    @Operation(summary = "Danh sách, Phân trang, tìm kiếm, lọc hóa đơn")
    @GetMapping
    public ApiResponse<List<InvoiceResponse>> getPageAndSearchAndFilter(
            @ModelAttribute InvoiceFilter filter,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "15") int size) {
        PaginatedResponse<InvoiceResponse> result =
                invoiceService.getPageAndSearchAndFilterByUserId(filter, page, size);

        return ApiResponse.<List<InvoiceResponse>>builder()
                .message("Get invoices successfully")
                .data(result.getData())
                .meta(result.getMeta())
                .build();
    }

    @Operation(summary = "Danh sách, Phân trang, tìm kiếm, lọc hóa đơn đã hủy")
    @GetMapping("/cancel")
    public ApiResponse<List<InvoiceResponse>> getInvoiceWithStatusCancel(
            @ModelAttribute InvoiceFilter filter,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "15") int size) {
        PaginatedResponse<InvoiceResponse> result =
                invoiceService.getInvoiceWithStatusCancelByUserId(filter, page, size);

        return ApiResponse.<List<InvoiceResponse>>builder()
                .message("Get canceled invoices successfully")
                .data(result.getData())
                .meta(result.getMeta())
                .build();
    }

    @Operation(summary = "Xem chi tiết hóa đơn")
    @GetMapping("/{invoiceId}")
    public ApiResponse<InvoiceDetailsResponse> getInvoiceDetails(@PathVariable("invoiceId") String invoiceId) {
        InvoiceDetailsResponse response = invoiceService.getInvoiceDetails(invoiceId);

        return ApiResponse.<InvoiceDetailsResponse>builder()
                .message("Get invoice details successfully")
                .data(response)
                .build();
    }

    @Operation(summary = "Tạo hóa đơn theo hợp đồng")
    @PostMapping("/by-contract")
    public ApiResponse<InvoiceResponse> createInvoiceForContract(@Valid @RequestBody InvoiceCreationRequest request) {
        InvoiceResponse response = invoiceService.createInvoiceForContract(request);

        return ApiResponse.<InvoiceResponse>builder()
                .message("Create invoice successfully")
                .data(response)
                .build();
    }

    @Operation(summary = "Tạo hóa đơn theo tòa nhà")
    @PostMapping("/by-building")
    public ApiResponse<List<InvoiceResponse>> createInvoicesForBuilding(
            @Valid @RequestBody InvoiceBuildingCreationRequest request) {
        List<InvoiceResponse> responses = invoiceService.createInvoicesForBuilding(request);

        return ApiResponse.<List<InvoiceResponse>>builder()
                .message("Create invoice by building successfully")
                .data(responses)
                .build();
    }

    @Operation(summary = "Tạo hóa đơn theo tầng")
    @PostMapping("/by-floor")
    public ApiResponse<List<InvoiceResponse>> createInvoicesForFloor(
            @Valid @RequestBody InvoiceFloorCreationRequest request) {
        List<InvoiceResponse> responses = invoiceService.createInvoicesForFloor(request);

        return ApiResponse.<List<InvoiceResponse>>builder()
                .message("Create invoice by floor successfully")
                .data(responses)
                .build();
    }

    @Operation(summary = "Tạo hóa đơn cuối cùng")
    @PostMapping("/finalize")
    public ApiResponse<InvoiceResponse> createFinalInvoice(@Valid @RequestBody InvoiceCreationRequest request) {
        InvoiceResponse response = invoiceService.createEndOfMonthInvoice(request);

        return ApiResponse.<InvoiceResponse>builder()
                .message("Create final invoice successfully")
                .data(response)
                .build();
    }

    @Operation(summary = "Cập nhật hóa đơn")
    @PutMapping("/{invoiceId}")
    public ApiResponse<InvoiceResponse> updateInvoice(
            @Valid @RequestBody InvoiceUpdateRequest request, @PathVariable("invoiceId") String invoiceId) {
        InvoiceResponse response = invoiceService.updateInvoice(invoiceId, request);

        return ApiResponse.<InvoiceResponse>builder()
                .message("Update invoice successfully")
                .data(response)
                .build();
    }

    @Operation(summary = "Thống kê hóa đơn")
    @GetMapping("/statistics")
    public ApiResponse<InvoiceStatistics> getInvoiceStatistics() {
        return ApiResponse.<InvoiceStatistics>builder()
                .message("Get invoice statistics successfully")
                .data(invoiceService.getInvoiceStatistics())
                .build();
    }

    @Operation(summary = "Chuyển đổi trạng thái hóa đơn")
    @PutMapping("/toggle/{invoiceId}")
    public ApiResponse<String> toggleInvoiceStatus(@PathVariable("invoiceId") String invoiceId) {
        invoiceService.toggleInvoiceStatus(invoiceId);

        return ApiResponse.<String>builder()
                .message("Toggle invoice status successfully")
                .data("Invoice with ID " + invoiceId + " status has been toggled.")
                .build();
    }

    @Operation(summary = "Xóa mềm hóa đơn")
    @PutMapping("/soft/{invoiceId}")
    public ApiResponse<String> softDeleteInvoice(@PathVariable("invoiceId") String invoiceId) {
        invoiceService.softDeleteInvoice(invoiceId);

        return ApiResponse.<String>builder()
                .message("Soft delete invoice successfully")
                .data("Invoice with ID " + invoiceId + " has been soft deleted.")
                .build();
    }

    @Operation(summary = "Xóa hóa đơn")
    @DeleteMapping("/{invoiceId}")
    public ApiResponse<String> deleteInvoice(@PathVariable("invoiceId") String invoiceId) {
        invoiceService.deleteInvoice(invoiceId);

        return ApiResponse.<String>builder()
                .message("Delete invoice successfully")
                .data("Invoice with ID " + invoiceId + " has been deleted.")
                .build();
    }

    @Operation(summary = "Lấy danh sách hóa đơn theo user ID")
    @GetMapping("/all")
    public ApiResponse<List<InvoiceResponse>> getAllInvoices() {
        List<InvoiceResponse> invoices = invoiceService.getAllInvoicesByUserId();

        return ApiResponse.<List<InvoiceResponse>>builder()
                .message("Get all invoices successfully")
                .data(invoices)
                .build();
    }

    @Operation(summary = "Gửi thông báo thanh toán hóa đơn tháng tới khách hàng")
    @PostMapping("/send-payment-notice")
    public ApiResponse<PaymentBatchResponse> generatePaymentReceiptsForCurrentMonth() {
        PaymentBatchResponse response = invoiceService.generateMonthlyPaymentRequests();
        return ApiResponse.<PaymentBatchResponse>builder()
                .message("Create and send payment receipt successfully")
                .data(response)
                .build();
    }

    @Operation(summary = "Xác nhận phương thức thanh toán")
    @PatchMapping("/payments/{receiptId}")
    public ApiResponse<PaymentMethodResponse> confirmPaymentMethod(
            @PathVariable("receiptId") String receiptId,
            @RequestBody PaymentMethodRequest request
    ) {
        return ApiResponse.<PaymentMethodResponse>builder()
                .message("Confirm payment method successfully")
                .data(invoiceService.confirmPaymentMethod(receiptId, request))
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
                .data(invoiceService.rejectPaymentReceipt(receiptId, request))
                .build();
    }

    @Operation(summary = "Xác nhận thanh toán toán cho phiếu thanh toán bằng tiền mặt")
    @PatchMapping("/payment-confirm/{receiptId}")
    public ApiResponse<String> confirmCashPayment(@PathVariable("receiptId") String receiptId) {
        invoiceService.confirmCashPayment(receiptId);

        return ApiResponse.<String>builder()
                .message("Confirm cash payment successfully")
                .data("Confirmation of paid " + receiptId + " invoice")
                .build();
    }

    @Operation(summary = "Danh sách, Phân trang, tìm kiếm, lọc hóa đơn dành cho màn khách")
    @GetMapping("/tenant")
    public ApiResponse<List<InvoiceResponse>> getInvoicesForTenant(
            @ModelAttribute InvoiceFilter filter,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "15") int size) {
        PaginatedResponse<InvoiceResponse> result =
                invoiceService.getInvoicesForTenant(filter, page, size);

        return ApiResponse.<List<InvoiceResponse>>builder()
                .message("Get invoices for tenant successfully")
                .data(result.getData())
                .meta(result.getMeta())
                .build();
    }
}
