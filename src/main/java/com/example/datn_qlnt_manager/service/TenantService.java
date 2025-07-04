package com.example.datn_qlnt_manager.service;

import com.example.datn_qlnt_manager.dto.PaginatedResponse;
import com.example.datn_qlnt_manager.dto.filter.TenantFilter;
import com.example.datn_qlnt_manager.dto.request.tenant.TenantCreationRequest;
import com.example.datn_qlnt_manager.dto.request.tenant.TenantUpdateRequest;
import com.example.datn_qlnt_manager.dto.response.tenant.TenantDetailResponse;
import com.example.datn_qlnt_manager.dto.response.tenant.TenantResponse;
import com.example.datn_qlnt_manager.dto.statistics.TenantStatistics;

import java.util.List;

public interface TenantService {
    PaginatedResponse<TenantResponse> getPageAndSearchAndFilterTenantByUserId(TenantFilter filter, int page, int size);

    PaginatedResponse<TenantResponse> getTenantWithStatusCancelByUserId(TenantFilter filter, int page, int size);

    TenantResponse createTenantByOwner(TenantCreationRequest request);

    TenantResponse createTenantByRepresentative(TenantCreationRequest request);

    TenantResponse updateTenant(String tenantId, TenantUpdateRequest request);

    TenantDetailResponse getTenantDetailById(String tenantId);

    TenantStatistics getTenantStatisticsByUserId();

    List<TenantResponse> getAllTenantsByUserId();

    void toggleTenantStatusById(String tenantId);

    void softDeleteTenantById(String tenantId);

    void deleteTenantById(String tenantId);
}
