package com.example.datn_qlnt_manager.service;

import com.example.datn_qlnt_manager.dto.PaginatedResponse;
import com.example.datn_qlnt_manager.dto.filter.TenantFilter;
import com.example.datn_qlnt_manager.dto.request.tenant.TenantCreationRequest;
import com.example.datn_qlnt_manager.dto.request.tenant.TenantUpdateRequest;
import com.example.datn_qlnt_manager.dto.response.tenant.TenantResponse;

public interface TenantService {
    PaginatedResponse<TenantResponse> filterTenants(TenantFilter filter, int page, int size);

    TenantResponse createTenant(TenantCreationRequest request);

    TenantResponse updateTenant(String tenantId, TenantUpdateRequest request);

    TenantResponse getTenantById(String tenantId);

    void deleteTenantById(String tenantId);
}
