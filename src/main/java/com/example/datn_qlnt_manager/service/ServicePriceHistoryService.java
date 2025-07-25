package com.example.datn_qlnt_manager.service;

import com.example.datn_qlnt_manager.dto.PaginatedResponse;
import com.example.datn_qlnt_manager.dto.filter.ServicePriceHistoryFilter;
import com.example.datn_qlnt_manager.dto.response.servicePriceHistory.ServicePriceHistoryResponse;

public interface ServicePriceHistoryService {
    public PaginatedResponse<ServicePriceHistoryResponse> getServicePriceHistories(
            int page, int size, ServicePriceHistoryFilter filter);
}
