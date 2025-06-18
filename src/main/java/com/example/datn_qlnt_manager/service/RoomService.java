package com.example.datn_qlnt_manager.service;

import java.util.List;
import java.util.UUID;

import com.example.datn_qlnt_manager.common.RoomStatus;
import com.example.datn_qlnt_manager.dto.ApiResponse;
import com.example.datn_qlnt_manager.dto.request.filter.RoomFilter;
import com.example.datn_qlnt_manager.dto.request.room.RoomCreationRequest;
import com.example.datn_qlnt_manager.dto.request.room.RoomDeleteRequest;
import com.example.datn_qlnt_manager.dto.request.room.RoomUpdateRequest;
import com.example.datn_qlnt_manager.dto.response.room.RoomResponse;

public interface RoomService {

    ApiResponse<List<RoomResponse>> findAll(
            Integer page,
            Integer size,
            RoomFilter roomFilter);


    RoomResponse createRoom(RoomCreationRequest request);

    RoomResponse updateRoom(UUID roomId, RoomUpdateRequest request);

    RoomResponse deleteRoom(UUID roomId, RoomDeleteRequest request);

    RoomResponse updateRoomStatus(UUID roomId, RoomStatus status);

}
