package com.example.datn_qlnt_manager.service;

import java.util.UUID;

import com.example.datn_qlnt_manager.common.RoomStatus;
import com.example.datn_qlnt_manager.dto.PaginatedResponse;
import com.example.datn_qlnt_manager.dto.request.filter.RoomFilter;
import com.example.datn_qlnt_manager.dto.request.room.RoomCreationRequest;
import com.example.datn_qlnt_manager.dto.request.room.RoomUpdateRequest;
import com.example.datn_qlnt_manager.dto.response.room.RoomResponse;

public interface RoomService {

    PaginatedResponse<RoomResponse> filterRooms( Integer page, Integer size, RoomFilter roomFilter);

    RoomResponse createRoom(RoomCreationRequest request);

    RoomResponse updateRoom(String roomId, RoomUpdateRequest request);

    Void deleteRoom(String roomId);

    RoomResponse updateRoomStatus(String roomId, RoomStatus status);

}
