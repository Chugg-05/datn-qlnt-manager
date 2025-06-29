package com.example.datn_qlnt_manager.service;

import com.example.datn_qlnt_manager.common.RoomStatus;
import com.example.datn_qlnt_manager.dto.PaginatedResponse;
import com.example.datn_qlnt_manager.dto.filter.RoomFilter;
import com.example.datn_qlnt_manager.dto.request.room.RoomCreationRequest;
import com.example.datn_qlnt_manager.dto.request.room.RoomUpdateRequest;
import com.example.datn_qlnt_manager.dto.response.room.RoomCountResponse;
import com.example.datn_qlnt_manager.dto.response.room.RoomResponse;

public interface RoomService {

    PaginatedResponse<RoomResponse> filterRooms(Integer page, Integer size, RoomFilter roomFilter);

    RoomResponse createRoom(RoomCreationRequest request);

    RoomResponse updateRoom(String roomId, RoomUpdateRequest request);

    Void deleteRoom(String roomId);

    void softDeleteRoomById(String id);

    RoomResponse updateRoomStatus(String roomId, RoomStatus status);

    RoomCountResponse statisticsRoomByStatus(String floorId);

//    void toggleStatus(String id);

}
