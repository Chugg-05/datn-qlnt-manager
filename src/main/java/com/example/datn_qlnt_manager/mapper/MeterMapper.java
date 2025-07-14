package com.example.datn_qlnt_manager.mapper;

import com.example.datn_qlnt_manager.dto.request.meter.MeterCreationRequest;
import com.example.datn_qlnt_manager.dto.request.meter.MeterUpdateRequest;
import com.example.datn_qlnt_manager.dto.response.meter.MeterResponse;
import com.example.datn_qlnt_manager.entity.Meter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface MeterMapper {
    Meter toMeterCreation (MeterCreationRequest meter) ;

    @Mapping(source = "room.id", target = "roomId")
    @Mapping(source = "room.roomCode", target = "roomCode")
    @Mapping(source = "service.id", target = "serviceId")
    @Mapping(source = "service.name", target = "serviceName")
    @Mapping(source = "createdAt", target = "updatedAt")
    @Mapping(source = "updatedAt", target = "createdAt")
    MeterResponse toMeterResponse (Meter meter);

    void toMeterUpdate (@MappingTarget Meter meter, MeterUpdateRequest request) ;
}
