package com.example.datn_qlnt_manager.service;

import java.util.List;

import com.example.datn_qlnt_manager.dto.ApiResponse;
import com.example.datn_qlnt_manager.dto.response.room.RoomResponse;

public interface RoomService {

    ApiResponse<List<RoomResponse>> findAll(
            Integer page,
            Integer size,
            String status,
            Double maxPrice,
            Double minPrice,
            Double maxAcreage,
            Double minAcreage,
            Integer maxPerson,
            String nameFloor);
}
