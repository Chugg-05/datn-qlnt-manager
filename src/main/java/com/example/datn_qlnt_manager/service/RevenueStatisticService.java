package com.example.datn_qlnt_manager.service;

import com.example.datn_qlnt_manager.dto.statistics.revenue.RevenueStatisticRequest;
import com.example.datn_qlnt_manager.dto.statistics.revenue.RevenueStatisticResponse;

public interface RevenueStatisticService {
    RevenueStatisticResponse getRevenueStatistic(RevenueStatisticRequest request);
}
