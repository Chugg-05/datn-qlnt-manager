package com.example.datn_qlnt_manager.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.example.datn_qlnt_manager.dto.request.building.BuildingCreationRequest;
import com.example.datn_qlnt_manager.dto.request.building.BuildingUpdateRequest;
import com.example.datn_qlnt_manager.dto.response.building.BuildingResponse;
import com.example.datn_qlnt_manager.entity.Building;

@Mapper(componentModel = "spring")
public interface BuildingMapper {

    @Mapping(target = "buildingCode", ignore = true)
    @Mapping(target = "status", ignore = true)
    Building toBuilding(BuildingCreationRequest request);

    @Mapping(source = "user.fullName", target = "fullName")
    BuildingResponse toBuildingResponse(Building building);
    //    @Mapping(target = "status", ignore = true)
    void updateBuilding(@MappingTarget Building building, BuildingUpdateRequest request);
}
