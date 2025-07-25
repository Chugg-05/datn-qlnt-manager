package com.example.datn_qlnt_manager.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.datn_qlnt_manager.dto.response.servicePriceHistory.ServicePriceHistoryResponse;
import com.example.datn_qlnt_manager.entity.ServicePriceHistory;

@Mapper(componentModel = "spring")
public interface ServicePriceHistoryMapper {
    @Mapping(source = "service.name", target = "serviceName")
    ServicePriceHistoryResponse toResponse(ServicePriceHistory history);
}
