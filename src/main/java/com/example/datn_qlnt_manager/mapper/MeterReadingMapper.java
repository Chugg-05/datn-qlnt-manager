package com.example.datn_qlnt_manager.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.example.datn_qlnt_manager.dto.request.meterReading.MeterReadingCreationRequest;
import com.example.datn_qlnt_manager.dto.request.meterReading.MeterReadingUpdateRequest;
import com.example.datn_qlnt_manager.dto.response.meterReading.MeterReadingResponse;
import com.example.datn_qlnt_manager.entity.MeterReading;

@Mapper(componentModel = "spring")
public interface MeterReadingMapper {

    //    @Mapping(target = "id", ignore = true)

    MeterReading toMeterReadingCreation(MeterReadingCreationRequest request);

    @Mapping(target = "meterId", source = "meter.id")
//    @Mapping(target = "meterCode", source = "meter.meterCode")
//    @Mapping(target = "meterName", source = "meter.meterName")
    @Mapping(target = "meterType", source = "meter.meterType")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    @Mapping(source = "meter.room.roomCode", target = "roomCode")
    MeterReadingResponse toResponse(MeterReading meterReading);

    void toMeterReadingUpdate(@MappingTarget MeterReading meterReading, MeterReadingUpdateRequest request);
}
