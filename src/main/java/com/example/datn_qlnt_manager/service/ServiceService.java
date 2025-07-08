package com.example.datn_qlnt_manager.service;

import com.example.datn_qlnt_manager.common.ServiceStatus;
import com.example.datn_qlnt_manager.dto.PaginatedResponse;
import com.example.datn_qlnt_manager.dto.filter.ServiceFilter;
import com.example.datn_qlnt_manager.dto.request.service.ServiceCreationRequest;
import com.example.datn_qlnt_manager.dto.request.service.ServiceUpdateRequest;
import com.example.datn_qlnt_manager.dto.response.service.ServiceCountResponse;
import com.example.datn_qlnt_manager.dto.response.service.ServiceResponse;

public interface ServiceService {

    PaginatedResponse<ServiceResponse> getPageAndSearchAndFilterService(ServiceFilter serviceFilter, int page, int size);

    ServiceResponse createService(ServiceCreationRequest request);

    ServiceResponse updateService(String serviceId, ServiceUpdateRequest request);

    Void deleteService(String serviceId);

    void softDeleteServiceById(String id);

    ServiceResponse toggleServiceStatus(String serviceId, ServiceStatus status);

    ServiceCountResponse statisticsServiceByStatus();


}
