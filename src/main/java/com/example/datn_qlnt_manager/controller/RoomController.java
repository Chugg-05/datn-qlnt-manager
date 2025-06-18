package com.example.datn_qlnt_manager.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.example.datn_qlnt_manager.dto.ApiResponse;
import com.example.datn_qlnt_manager.dto.response.room.RoomResponse;
import com.example.datn_qlnt_manager.service.RoomService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/rooms")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Room", description = "API Room")
public class RoomController {

    RoomService roomService;

    @Operation(summary = "Phân trang, tìm kiếm, lọc phòng")
    @GetMapping
    public ApiResponse<List<RoomResponse>> findAll(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "15") Integer size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxAcreage,
            @RequestParam(required = false) Double minAcreage,
            @RequestParam(required = false) Integer maxPerson,
            @RequestParam(required = false) String nameFloor) {

        return roomService.findAll(
                page, size, status, maxPrice, minPrice, maxAcreage, minAcreage, maxPerson, nameFloor);
    }
}
