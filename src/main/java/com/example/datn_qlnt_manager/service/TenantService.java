package com.example.datn_qlnt_manager.service;

import java.util.List;

import com.example.datn_qlnt_manager.dto.PaginatedResponse;
import com.example.datn_qlnt_manager.dto.filter.TenantFilter;
import com.example.datn_qlnt_manager.dto.request.tenant.TenantCreationRequest;
import com.example.datn_qlnt_manager.dto.request.tenant.TenantUpdateRequest;
import com.example.datn_qlnt_manager.dto.response.tenant.TenantDetailResponse;
import com.example.datn_qlnt_manager.dto.response.tenant.TenantResponse;
import com.example.datn_qlnt_manager.dto.statistics.TenantStatistics;
import com.example.datn_qlnt_manager.entity.Tenant;
import jakarta.transaction.Transactional;
import org.springframework.web.multipart.MultipartFile;

public interface TenantService {
    PaginatedResponse<TenantResponse> getPageAndSearchAndFilterTenantByUserId(TenantFilter filter, int page, int size);

    PaginatedResponse<TenantResponse> getTenantWithStatusCancelByUserId(TenantFilter filter, int page, int size);

    TenantResponse createTenant(TenantCreationRequest request, MultipartFile frontCCCD, MultipartFile backCCCD);

    TenantResponse updateTenant(String tenantId, TenantUpdateRequest request, MultipartFile frontCCCD, MultipartFile backCCCD);

    TenantDetailResponse getTenantDetailById(String tenantId);

    TenantStatistics getTenantStatisticsByUserId();

    List<TenantResponse> getAllTenantsByUserId();

    void softDeleteTenantById(String tenantId);

    void deleteTenantById(String tenantId);

    List<TenantResponse> getTenantsByRoomId(String roomId);

    @Transactional
    void ensureTenantHasActiveUser(Tenant tenant);
}
