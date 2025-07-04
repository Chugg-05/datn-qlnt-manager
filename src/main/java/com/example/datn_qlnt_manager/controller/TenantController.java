package com.example.datn_qlnt_manager.controller;

import java.util.List;

import com.example.datn_qlnt_manager.dto.response.tenant.TenantDetailResponse;
import com.example.datn_qlnt_manager.dto.statistics.TenantStatistics;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;

import com.example.datn_qlnt_manager.dto.ApiResponse;
import com.example.datn_qlnt_manager.dto.PaginatedResponse;
import com.example.datn_qlnt_manager.dto.filter.TenantFilter;
import com.example.datn_qlnt_manager.dto.request.tenant.TenantCreationRequest;
import com.example.datn_qlnt_manager.dto.request.tenant.TenantUpdateRequest;
import com.example.datn_qlnt_manager.dto.response.tenant.TenantResponse;
import com.example.datn_qlnt_manager.service.TenantService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/tenants")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Tenant", description = "API Tenant")
public class TenantController {
    TenantService tenantService;

    @Operation(summary = "Danh sách, Phân trang, tìm kiếm, lọc khách hàng")
    @GetMapping
    public ApiResponse<List<TenantResponse>> getPageAndSearchAndFilterTenant(
            @ModelAttribute TenantFilter filter,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "15") int size) {

        PaginatedResponse<TenantResponse> result = tenantService.getPageAndSearchAndFilterTenantByUserId(filter, page, size);

        return ApiResponse.<List<TenantResponse>>builder()
                .message("Get tenants successfully")
                .data(result.getData())
                .meta(result.getMeta())
                .build();
    }

    @Operation(summary = "Danh sách, Phân trang, tìm kiếm, lọc khách hàng đã hủy (status = HUY_BO)")
    @GetMapping("/cancel")
    public ApiResponse<List<TenantResponse>> getTenantWithStatusCancel(
            @ModelAttribute TenantFilter filter,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "15") int size) {

        PaginatedResponse<TenantResponse> result = tenantService.getTenantWithStatusCancelByUserId(filter, page, size);

        return ApiResponse.<List<TenantResponse>>builder()
                .message("Get canceled tenants successfully")
                .data(result.getData())
                .meta(result.getMeta())
                .build();
    }

    @Operation(summary = "Thêm khách hàng mới dành cho chủ nhà trọ")
    @PostMapping("/owner")
    public ApiResponse<TenantResponse> createTenantByOwner(
            @Valid @RequestBody TenantCreationRequest request) {
        TenantResponse response = tenantService.createTenantByOwner(request);
        return ApiResponse.<TenantResponse>builder()
                .message("Tenant created successfully by owner")
                .data(response)
                .build();
    }

    @Operation(summary = "Thêm khách hàng mới dành cho khach hàng đại diện")
    @PostMapping("/representative")
    public ApiResponse<TenantResponse> createTenantByRepresentative(
            @Valid @RequestBody TenantCreationRequest request) {
        TenantResponse response = tenantService.createTenantByRepresentative(request);
        return ApiResponse.<TenantResponse>builder()
                .message("Representative tenant created successfully")
                .data(response)
                .build();
    }

    @Operation(summary = "Update thông tin khách hàng mới dành cho user")
    @PutMapping("/{tenantId}")
    public ApiResponse<TenantResponse> updateTenant(
            @Valid @RequestBody TenantUpdateRequest request, @PathVariable("tenantId") String tenantId) {

        return ApiResponse.<TenantResponse>builder()
                .message("Tenant updated successfully")
                .data(tenantService.updateTenant(tenantId, request))
                .build();
    }

    @GetMapping("/detail/{tenantId}")
    public ApiResponse<TenantDetailResponse> getTenantDetail(@PathVariable String tenantId) {
        TenantDetailResponse response = tenantService.getTenantDetailById(tenantId);
        return ApiResponse.<TenantDetailResponse>builder()
                .message("Tenant detail found successfully")
                .data(response)
                .build();
    }

    @Operation(summary = "Lấy danh sách khách hàng theo user ID")
    @GetMapping("/all")
    public ApiResponse<List<TenantResponse>> getAllTenants() {
        List<TenantResponse> tenants = tenantService.getAllTenantsByUserId();

        return ApiResponse.<List<TenantResponse>>builder()
                .message("All tenants retrieved successfully")
                .data(tenants)
                .build();
    }

    @Operation(summary = "Thống kê khách hàng theo trạng thái")
    @GetMapping("/statistics")
    public ApiResponse<TenantStatistics> getTenantStatistics() {

        return ApiResponse.<TenantStatistics>builder()
                .message("Tenant statistics successfully")
                .data(tenantService.getTenantStatisticsByUserId())
                .build();
    }

    @Operation(summary = "Chuyển đổi trạng thái khách hàng")
    @PutMapping("/toggle/{tenantId}")
    public ApiResponse<String> toggleTenantStatus(@PathVariable("tenantId") String tenantId) {
        tenantService.toggleTenantStatusById(tenantId);
        return ApiResponse.<String>builder()
                .message("Tenant status successfully")
                .data("Tenant with ID " + tenantId + " status has been toggled.")
                .build();
    }

    @Operation(summary = "Xóa mềm khách hàng")
    @PutMapping("/soft/{tenantId}")
    public ApiResponse<String> softDeleteTenant(@PathVariable("tenantId") String tenantId) {
        tenantService.softDeleteTenantById(tenantId);
        return ApiResponse.<String>builder()
                .message("Tenant soft deleted successfully")
                .data("Tenant with ID " + tenantId + " has been soft deleted.")
                .build();
    }

    @Operation(summary = "Xóa khách hàng")
    @DeleteMapping("/{tenantId}")
    public ApiResponse<String> deleteTenant(@PathVariable("tenantId") String tenantId) {
        tenantService.deleteTenantById(tenantId);
        return ApiResponse.<String>builder()
                .message("Tenant deleted successfully")
                .data("Tenant with ID " + tenantId + " has been deleted.")
                .build();
    }
}
