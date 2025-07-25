package com.example.datn_qlnt_manager.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.datn_qlnt_manager.dto.request.service.ServiceCreationRequest;
import com.example.datn_qlnt_manager.dto.request.service.ServiceUpdateRequest;
import com.example.datn_qlnt_manager.dto.response.service.ServiceResponse;
import com.example.datn_qlnt_manager.entity.Service;

@Mapper
public interface ServiceMapper {
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    ServiceResponse toServiceResponse(Service service);

    //   @Mapping(target = "", ignore = true)
    Service toServiceCreation(ServiceCreationRequest request);

    Service toServiceUpdate(ServiceUpdateRequest request);
}
