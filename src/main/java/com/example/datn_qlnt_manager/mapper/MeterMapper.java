package com.example.datn_qlnt_manager.mapper;

import com.example.datn_qlnt_manager.dto.request.meter.ChangeMeterRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.example.datn_qlnt_manager.dto.request.meter.MeterCreationRequest;
import com.example.datn_qlnt_manager.dto.request.meter.MeterUpdateRequest;
import com.example.datn_qlnt_manager.dto.response.meter.MeterResponse;
import com.example.datn_qlnt_manager.entity.Meter;

@Mapper(componentModel = "spring")
public interface MeterMapper {
    Meter toMeterCreation(MeterCreationRequest meter);

    Meter toMeterChange(ChangeMeterRequest request);

    @Mapping(source = "room.id", target = "roomId")
    @Mapping(source = "room.roomCode", target = "roomCode")
    @Mapping(source = "service.id", target = "serviceId")
    @Mapping(source = "service.name", target = "serviceName")
    @Mapping(source = "createdAt", target = "updatedAt")
    @Mapping(source = "updatedAt", target = "createdAt")
    MeterResponse toMeterResponse(Meter meter);

    void toMeterUpdate(@MappingTarget Meter meter, MeterUpdateRequest request);
}
