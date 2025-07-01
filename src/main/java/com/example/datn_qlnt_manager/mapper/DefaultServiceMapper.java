package com.example.datn_qlnt_manager.mapper;

import com.example.datn_qlnt_manager.dto.request.defaultService.DefaultServiceCreationRequest;
import com.example.datn_qlnt_manager.dto.request.defaultService.DefaultServiceUpdateRequest;
import com.example.datn_qlnt_manager.dto.response.defaultService.DefaultServiceResponse;
import com.example.datn_qlnt_manager.entity.DefaultService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;


@Mapper(componentModel = "spring")
public interface DefaultServiceMapper {

    @Mapping(target = "defaultServiceStatus", ignore = true)
    DefaultService toDefaultService (DefaultServiceCreationRequest request);

    @Mapping(source = "building.buildingName", target = "buildingName")
    @Mapping(source = "floor.nameFloor", target = "floorName")
    @Mapping(source = "service.name", target = "serviceName")
    DefaultServiceResponse toDefaultServiceResponse (DefaultService defaultService);

    void updateDefaultService (@MappingTarget DefaultService defaultService, DefaultServiceUpdateRequest request);
}
