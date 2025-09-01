package com.example.datn_qlnt_manager.service;

import com.example.datn_qlnt_manager.dto.statistics.revenue.request.RevenueStatisticRequest;
import com.example.datn_qlnt_manager.dto.statistics.revenue.response.RevenueComparisonResponse;

import java.util.List;

public interface RevenueStatisticService {
    List<RevenueComparisonResponse> compareRevenueByBuilding(RevenueStatisticRequest request);

}
