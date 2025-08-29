package com.example.datn_qlnt_manager.service;

import java.math.BigDecimal;
import java.util.List;

import com.example.datn_qlnt_manager.common.RoomStatus;
import com.example.datn_qlnt_manager.common.RoomType;
import com.example.datn_qlnt_manager.dto.PaginatedResponse;
import com.example.datn_qlnt_manager.dto.filter.MeterFilter;
import com.example.datn_qlnt_manager.dto.filter.MeterInitFilterResponse;
import com.example.datn_qlnt_manager.dto.request.meter.ChangeMeterRequest;
import com.example.datn_qlnt_manager.dto.request.meter.MeterCreationRequest;
import com.example.datn_qlnt_manager.dto.request.meter.MeterUpdateRequest;
import com.example.datn_qlnt_manager.dto.response.IdAndName;
import com.example.datn_qlnt_manager.dto.response.meter.CreateMeterInitResponse;
import com.example.datn_qlnt_manager.dto.response.meter.MeterReadingMonthlyStatsResponse;
import com.example.datn_qlnt_manager.dto.response.meter.MeterResponse;
import com.example.datn_qlnt_manager.dto.response.meter.RoomNoMeterResponse;
import com.example.datn_qlnt_manager.dto.statistics.RoomNoMeterCountStatistics;

public interface MeterService {

    PaginatedResponse<MeterResponse> getPageAndSearchAndFilterMeterByUserId(
            MeterFilter meterFilter, int page, int size);

    MeterResponse createMeter(MeterCreationRequest request);

    MeterResponse updateMeter(String meterId, MeterUpdateRequest request);

    void deleteMeter(String meterId);

    List<MeterReadingMonthlyStatsResponse> getMonthlyStats(String roomId);

    CreateMeterInitResponse getMeterInfoByUserId(String buildingId);

    MeterInitFilterResponse getMeterFilterByUserId(String buildingId);

    List<IdAndName> findAllMeters();

    PaginatedResponse<RoomNoMeterResponse> getRoomsWithoutMeterByUser(
            Integer page,
            Integer size,
            String query,
            RoomStatus status,
            RoomType roomType,
            BigDecimal minPrice,
            BigDecimal maxPrice);

    RoomNoMeterCountStatistics countRoomsWithoutMeterByUser();

    MeterResponse changeMeter(ChangeMeterRequest request, String meterId);
}
