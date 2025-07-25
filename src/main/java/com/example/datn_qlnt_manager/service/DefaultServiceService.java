package com.example.datn_qlnt_manager.service;

import com.example.datn_qlnt_manager.dto.PaginatedResponse;
import com.example.datn_qlnt_manager.dto.filter.DefaultServiceFilter;
import com.example.datn_qlnt_manager.dto.request.defaultService.DefaultServiceCreationRequest;
import com.example.datn_qlnt_manager.dto.request.defaultService.DefaultServiceUpdateRequest;
import com.example.datn_qlnt_manager.dto.response.defaultService.DefaultServiceInitResponse;
import com.example.datn_qlnt_manager.dto.response.defaultService.DefaultServiceResponse;
import com.example.datn_qlnt_manager.dto.statistics.DefaultServiceStatistics;

public interface DefaultServiceService {
    PaginatedResponse<DefaultServiceResponse> getPageAndSearchAndFilterDefaultServiceByUserId(
            DefaultServiceFilter filter, int page, int size);

    PaginatedResponse<DefaultServiceResponse> getDefaultServiceWithStatusCancelByUserId(
            DefaultServiceFilter filter, int page, int size);

    DefaultServiceResponse createDefaultService(DefaultServiceCreationRequest request);

    DefaultServiceResponse updateDefaultService(String defaultServiceId, DefaultServiceUpdateRequest request);

    void deleteDefaultServiceById(String defaultServiceId);

    DefaultServiceInitResponse initDefaultService();

    void softDeleteDefaultServiceById(String defaultServiceId);

    void toggleStatus(String defaultServiceId);

    DefaultServiceStatistics statisticsDefaultServiceByStatus();
}
