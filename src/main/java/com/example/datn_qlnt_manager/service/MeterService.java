package com.example.datn_qlnt_manager.service;

import com.example.datn_qlnt_manager.common.MeterType;
import com.example.datn_qlnt_manager.dto.PaginatedResponse;
import com.example.datn_qlnt_manager.dto.filter.MeterFilter;
import com.example.datn_qlnt_manager.dto.request.meter.MeterCreationRequest;
import com.example.datn_qlnt_manager.dto.request.meter.MeterUpdateRequest;
import com.example.datn_qlnt_manager.dto.response.meter.MeterResponse;

public interface MeterService {
    PaginatedResponse<MeterResponse> filterMeter(Integer page, Integer size, MeterFilter meterFilter);

    MeterResponse createMeter(MeterCreationRequest request);

    MeterResponse updateMeter(String id, MeterUpdateRequest request);

    Void deleteMeter(String id);

}
