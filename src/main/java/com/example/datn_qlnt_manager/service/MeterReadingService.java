package com.example.datn_qlnt_manager.service;

import com.example.datn_qlnt_manager.common.MeterType;
import com.example.datn_qlnt_manager.dto.request.meterReading.MeterReadingCreationRequest;
import com.example.datn_qlnt_manager.dto.request.meterReading.MeterReadingUpdateRequest;
import com.example.datn_qlnt_manager.dto.response.meterReading.MeterReadingResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MeterReadingService {

    MeterReadingResponse createMeterReading(MeterReadingCreationRequest request);

    MeterReadingResponse updateMeterReading(String id, MeterReadingUpdateRequest request);

    void deleteMeterReading(String id);

    MeterReadingResponse getMeterReadingById(String id);

    Page<MeterReadingResponse> filterMeterReadings(String buildingId, String roomCode, MeterType meterType, Integer month, Pageable pageable);
}
