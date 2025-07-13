package com.example.datn_qlnt_manager.service;

import com.example.datn_qlnt_manager.dto.PaginatedResponse;
import com.example.datn_qlnt_manager.dto.filter.MeterFilter;
import com.example.datn_qlnt_manager.dto.request.meter.MeterCreationRequest;
import com.example.datn_qlnt_manager.dto.request.meter.MeterUpdateRequest;
import com.example.datn_qlnt_manager.dto.response.meter.MeterReadingMonthlyStatsResponse;
import com.example.datn_qlnt_manager.dto.response.meter.MeterResponse;

import java.util.List;

public interface MeterService {

    PaginatedResponse<MeterResponse> getPageAndSearchAndFilterMeterByUserId(MeterFilter meterFilter, int page, int size);

    MeterResponse createMeter(MeterCreationRequest request);

    MeterResponse updateMeter(String meterId, MeterUpdateRequest request);

    void deleteMeter(String meterId);

    List<MeterReadingMonthlyStatsResponse> getMonthlyStats(String roomCode);

}