package com.example.datn_qlnt_manager.service;

import java.util.List;

import com.example.datn_qlnt_manager.common.RoomStatus;
import com.example.datn_qlnt_manager.dto.PaginatedResponse;
import com.example.datn_qlnt_manager.dto.filter.RoomFilter;
import com.example.datn_qlnt_manager.dto.request.room.RoomCreationRequest;
import com.example.datn_qlnt_manager.dto.request.room.RoomUpdateRequest;
import com.example.datn_qlnt_manager.dto.response.room.RoomCountResponse;
import com.example.datn_qlnt_manager.dto.response.room.RoomResponse;

public interface RoomService {

    PaginatedResponse<RoomResponse> getPageAndSearchAndFilterRoomByUserId(
            RoomFilter roomFilter, Integer page, Integer size);

    PaginatedResponse<RoomResponse> getRoomWithStatusCancelByUserId(RoomFilter roomFilter, Integer page, Integer size);

    RoomResponse createRoom(RoomCreationRequest request);

    RoomResponse updateRoom(String roomId, RoomUpdateRequest request);

    List<RoomResponse> getAllRoomsByUserId();

    Void deleteRoom(String roomId);

    void softDeleteRoomById(String id);

    RoomResponse updateRoomStatus(String roomId, RoomStatus status);

    RoomCountResponse statisticsRoomByStatus(String buildingId);
}
