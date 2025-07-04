package com.example.datn_qlnt_manager.service;

import com.example.datn_qlnt_manager.dto.PaginatedResponse;
import com.example.datn_qlnt_manager.dto.filter.ServiceRoomFilter;
import com.example.datn_qlnt_manager.dto.request.serviceRoom.ServiceRoomCreationRequest;
import com.example.datn_qlnt_manager.dto.request.serviceRoom.ServiceRoomUpdateRequest;
import com.example.datn_qlnt_manager.dto.response.serviceRoom.ServiceRoomResponse;
import com.example.datn_qlnt_manager.dto.statistics.ServiceRoomStatistics;

public interface ServiceRoomService {
    ServiceRoomResponse createServiceRoom(ServiceRoomCreationRequest request);

    ServiceRoomResponse updateServiceRoom(String serviceRoomId,ServiceRoomUpdateRequest request);

    void softDeleteServiceRoom(String serviceRoomId);

    void deleteServiceRoom(String serviceRoomId);

    PaginatedResponse<ServiceRoomResponse> filterServiceRooms(ServiceRoomFilter filter, int page, int size);

    ServiceRoomStatistics getServiceRoomStatusStatistics();



}
