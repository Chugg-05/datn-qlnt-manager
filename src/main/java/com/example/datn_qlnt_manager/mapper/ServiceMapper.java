package com.example.datn_qlnt_manager.mapper;

import com.example.datn_qlnt_manager.dto.request.service.ServiceCreationRequest;
import com.example.datn_qlnt_manager.dto.request.service.ServiceUpdateRequest;
import com.example.datn_qlnt_manager.dto.response.service.ServiceResponse;
import com.example.datn_qlnt_manager.entity.Service;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface ServiceMapper {
    ServiceResponse toServiceResponse(Service service);

 //   @Mapping(target = "", ignore = true)
    Service toServiceCreation(ServiceCreationRequest request);

    Service toServiceUpdate(ServiceUpdateRequest request);
}
