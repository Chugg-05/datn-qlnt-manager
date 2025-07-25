package com.example.datn_qlnt_manager.service;

import com.example.datn_qlnt_manager.dto.PaginatedResponse;
import com.example.datn_qlnt_manager.dto.filter.MeterReadingFilter;
import com.example.datn_qlnt_manager.dto.request.meterReading.MeterReadingCreationRequest;
import com.example.datn_qlnt_manager.dto.request.meterReading.MeterReadingUpdateRequest;
import com.example.datn_qlnt_manager.dto.response.meterReading.MeterReadingResponse;

public interface MeterReadingService {

    PaginatedResponse<MeterReadingResponse> getPageAndSearchAndFilterMeterReadingByUserId(
            MeterReadingFilter meterReadingFilter, int page, int size);

    MeterReadingResponse createMeterReading(MeterReadingCreationRequest request);

    MeterReadingResponse updateMeterReading(String id, MeterReadingUpdateRequest request);

    void deleteMeterReading(String meterReadingId);

    MeterReadingResponse getMeterReadingById(String meterReadingId);
}
