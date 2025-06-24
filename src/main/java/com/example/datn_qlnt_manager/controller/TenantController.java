package com.example.datn_qlnt_manager.controller;

import com.example.datn_qlnt_manager.dto.ApiResponse;
import com.example.datn_qlnt_manager.dto.PaginatedResponse;
import com.example.datn_qlnt_manager.dto.filter.TenantFilter;
import com.example.datn_qlnt_manager.dto.request.tenant.TenantCreationRequest;
import com.example.datn_qlnt_manager.dto.request.tenant.TenantUpdateRequest;
import com.example.datn_qlnt_manager.dto.response.tenant.TenantResponse;
import com.example.datn_qlnt_manager.service.TenantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/tenants")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Tenant", description = "API Tenant")
public class TenantController {
    TenantService tenantService;

    @Operation(summary = "Phân trang, tìm kiếm, lọc người dùng")
    @GetMapping
    public ApiResponse<List<TenantResponse>> filterTenants(
            @ModelAttribute TenantFilter filter,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "15") int size) {

        PaginatedResponse<TenantResponse> result = tenantService.filterTenants(filter, page, size);

        return ApiResponse.<List<TenantResponse>>builder()
                .message("Filter tenants successfully")
                .data(result.getData())
                .meta(result.getMeta())
                .build();

    }

    @Operation(summary = "Lấy thông tin khách hàng theo ID")
    @GetMapping("/{tenantId}")
    public ApiResponse<TenantResponse> getTenant(@PathVariable("tenantId") String tenantId) {
        TenantResponse tenantResponse = tenantService.getTenantById(tenantId);
        return ApiResponse.<TenantResponse>builder()
                .message("Tenant found successfully")
                .data(tenantResponse)
                .build();
    }

    @Operation(summary = "Thêm khách hàng mới dành cho user")
    @PostMapping
    public ApiResponse<TenantResponse> createTenant(@Valid @RequestBody TenantCreationRequest request) {
        TenantResponse response = tenantService.createTenant(request);

        return ApiResponse.<TenantResponse>builder()
                .message("Tenant created successfully")
                .data(response)
                .build();
    }

    @Operation(summary = "Update thông tin khách hàng mới dành cho user")
    @PutMapping("/{tenantId}")
    public ApiResponse<TenantResponse> updateTenant(
            @Valid
            @RequestBody TenantUpdateRequest request,
            @PathVariable("tenantId") String tenantId) {

        return ApiResponse.<TenantResponse>builder()
                .message("Tenant updated successfully")
                .data(tenantService.updateTenant(tenantId, request))
                .build();
    }

    @Operation(summary = "Xoa khách hàng")
    @DeleteMapping("/{tenantId}")
    public ApiResponse<String> deleteTenant(@PathVariable("tenantId") String tenantId) {
        tenantService.deleteTenantById(tenantId);
        return ApiResponse.<String>builder()
                .message("Tenant deleted successfully")
                .data("Tenant with ID " + tenantId + " has been deleted.")
                .build();
    }

}