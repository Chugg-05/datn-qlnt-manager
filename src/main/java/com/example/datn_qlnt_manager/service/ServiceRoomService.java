package com.example.datn_qlnt_manager.service;

import java.util.List;

import com.example.datn_qlnt_manager.dto.PaginatedResponse;
import com.example.datn_qlnt_manager.dto.filter.ServiceRoomFilter;
import com.example.datn_qlnt_manager.dto.projection.ServiceRoomView;
import com.example.datn_qlnt_manager.dto.request.service.ServiceUpdateUnitPriceRequest;
import com.example.datn_qlnt_manager.dto.request.serviceRoom.ServiceRoomCreationForBuildingRequest;
import com.example.datn_qlnt_manager.dto.request.serviceRoom.ServiceRoomCreationForRoomRequest;
import com.example.datn_qlnt_manager.dto.request.serviceRoom.ServiceRoomCreationForServiceRequest;
import com.example.datn_qlnt_manager.dto.request.serviceRoom.ServiceRoomCreationRequest;
import com.example.datn_qlnt_manager.dto.response.IdNameAndType;
import com.example.datn_qlnt_manager.dto.response.service.ServiceDetailResponse;
import com.example.datn_qlnt_manager.dto.response.service.ServiceUpdateUnitPriceResponse;
import com.example.datn_qlnt_manager.dto.response.serviceRoom.CreateRoomServiceInitResponse;
import com.example.datn_qlnt_manager.dto.response.serviceRoom.ServiceRoomDetailResponse;
import com.example.datn_qlnt_manager.dto.response.serviceRoom.ServiceRoomResponse;
import com.example.datn_qlnt_manager.dto.statistics.ServiceRoomStatistics;

public interface ServiceRoomService {
    PaginatedResponse<ServiceRoomView> getServiceRoomsPaging(ServiceRoomFilter filter, int page, int size);

    ServiceRoomDetailResponse getServiceRoomDetail(String roomId);

    ServiceRoomDetailResponse createRoomServiceForRoom(ServiceRoomCreationForRoomRequest request);

    ServiceDetailResponse createRoomServiceForService(ServiceRoomCreationForServiceRequest request);

    ServiceDetailResponse createRoomServiceForBuilding(ServiceRoomCreationForBuildingRequest request);

    ServiceRoomResponse createServiceRoom(ServiceRoomCreationRequest request);

    void deleteServiceRoom(String serviceRoomId);

    ServiceUpdateUnitPriceResponse updateServicePriceInBuilding(ServiceUpdateUnitPriceRequest request);

    ServiceRoomStatistics getServiceRoomStatusStatistics(String buildingId);

    void toggleServiceRoomStatus(String id);

    CreateRoomServiceInitResponse getServiceRoomInfoByUserId();

    List<IdNameAndType> getAllServiceRoomByUserId(String roomId);
}
