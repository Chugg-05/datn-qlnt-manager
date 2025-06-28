package com.example.datn_qlnt_manager.mapper;

import com.example.datn_qlnt_manager.dto.request.meter.MeterCreationRequest;
import com.example.datn_qlnt_manager.dto.request.meter.MeterUpdateRequest;
import com.example.datn_qlnt_manager.dto.response.meter.MeterResponse;
import com.example.datn_qlnt_manager.entity.Meter;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MeterMapper {
    MeterResponse toMeterResponse (Meter meter);
    Meter toMeterCreation (MeterCreationRequest meter) ;
    Meter toMeterUpdate (MeterUpdateRequest meter) ;
}
