package com.example.datn_qlnt_manager.controller;

import com.example.datn_qlnt_manager.dto.ApiResponse;
import com.example.datn_qlnt_manager.dto.PaginatedResponse;
import com.example.datn_qlnt_manager.dto.filter.InvoiceFilter;
import com.example.datn_qlnt_manager.dto.response.invoice.InvoiceResponse;
import com.example.datn_qlnt_manager.service.InvoiceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
            @RequestParam(defaultValue = "15") int size
    ) {
        PaginatedResponse<InvoiceResponse> result = invoiceService.getPageAndSearchAndFilter(filter, page, size);

        return ApiResponse.<List<InvoiceResponse>>builder()
                .message("Get invoices successfully")
                .data(result.getData())
                .meta(result.getMeta())
                .build();
    }
}
