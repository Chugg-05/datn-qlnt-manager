package com.example.datn_qlnt_manager.controller;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;

import com.example.datn_qlnt_manager.dto.ApiResponse;
import com.example.datn_qlnt_manager.dto.request.invoiceDetail.InvoiceDetailCreationRequest;
import com.example.datn_qlnt_manager.dto.request.invoiceDetail.InvoiceDetailUpdateRequest;
import com.example.datn_qlnt_manager.dto.response.invoice.InvoiceItemResponse;
import com.example.datn_qlnt_manager.service.InvoiceDetailService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/invoice-details")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Invoice Detail", description = "API Invoice Detail")
public class InvoiceDetailController {
    InvoiceDetailService invoiceDetailService;

    @PostMapping
    public ApiResponse<InvoiceItemResponse> createInvoiceDetail(
            @RequestBody @Valid InvoiceDetailCreationRequest request) {
        return ApiResponse.<InvoiceItemResponse>builder()
                .message("Invoice detail has been created successfully!")
                .data(invoiceDetailService.createInvoiceDetail(request))
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<InvoiceItemResponse> updateInvoiceDetail(
            @PathVariable("id") String detailId, @RequestBody @Valid InvoiceDetailUpdateRequest request) {
        return ApiResponse.<InvoiceItemResponse>builder()
                .message("Invoice detail has been updated successfully!")
                .data(invoiceDetailService.updateInvoiceDetail(detailId, request))
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteInvoiceDetail(@PathVariable("id") String detailId) {
        invoiceDetailService.deleteInvoiceDetail(detailId);

        return ApiResponse.<String>builder()
                .message("Invoice detail has been deleted successfully!")
                .data("Detail with ID: " + detailId + " has been deleted.")
                .build();
    }
}
