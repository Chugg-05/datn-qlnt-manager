package com.example.datn_qlnt_manager.mapper;

import com.example.datn_qlnt_manager.dto.request.floor.FloorUpdateRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.datn_qlnt_manager.dto.request.floor.FloorCreationRequest;
import com.example.datn_qlnt_manager.dto.response.floor.FloorResponse;
import com.example.datn_qlnt_manager.entity.Floor;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface FloorMapper {

    @Mapping(target = "status", constant = "HOAT_DONG")
    Floor toFloor(FloorCreationRequest request);

    @Mapping(target = "buildingId", source = "building.id")
    @Mapping(target = "buildingName", source = "building.buildingName")
    FloorResponse toResponse(Floor floor);

    @Mapping(target = "building", ignore = true)
    void updateFloor(FloorUpdateRequest request, @MappingTarget Floor floor);
}
