package com.example.datn_qlnt_manager.mapper;

import com.example.datn_qlnt_manager.dto.request.meterReading.MeterReadingCreationRequest;
import com.example.datn_qlnt_manager.dto.request.meterReading.MeterReadingUpdateRequest;
import com.example.datn_qlnt_manager.dto.response.meterReading.MeterReadingResponse;
import com.example.datn_qlnt_manager.entity.MeterReading;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MeterReadingMapper {

    MeterReadingResponse toResponse(MeterReading meterReading);

//    @Mapping(target = "id", ignore = true)
    MeterReading toMeterReadingCreation(MeterReadingCreationRequest request);

    MeterReading toMeterReadingUpdate(MeterReadingUpdateRequest request);
}
