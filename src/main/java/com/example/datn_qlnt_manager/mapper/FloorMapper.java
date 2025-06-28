package com.example.datn_qlnt_manager.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.example.datn_qlnt_manager.dto.request.floor.FloorCreationRequest;
import com.example.datn_qlnt_manager.dto.request.floor.FloorUpdateRequest;
import com.example.datn_qlnt_manager.dto.response.floor.FloorResponse;
import com.example.datn_qlnt_manager.entity.Floor;

@Mapper(componentModel = "spring")
public interface FloorMapper {

    @Mapping(target = "nameFloor", ignore = true)
    @Mapping(target = "status", constant = "HOAT_DONG")
    Floor toFloor(FloorCreationRequest request);

    @Mapping(target = "buildingId", source = "building.id")
    @Mapping(target = "buildingName", source = "building.buildingName")
    @Mapping(source = "createdAt", target = "createdAt")
    @Mapping(source = "updatedAt", target = "updatedAt")
    FloorResponse toResponse(Floor floor);

    @Mapping(target = "building", ignore = true)
    void updateFloor(FloorUpdateRequest request, @MappingTarget Floor floor);
}
