package com.example.datn_qlnt_manager.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;

import com.example.datn_qlnt_manager.common.RoomStatus;
import com.example.datn_qlnt_manager.dto.filter.RoomFilter;
import com.example.datn_qlnt_manager.dto.ApiResponse;
import com.example.datn_qlnt_manager.dto.PaginatedResponse;
import com.example.datn_qlnt_manager.dto.request.room.RoomCreationRequest;
import com.example.datn_qlnt_manager.dto.request.room.RoomUpdateRequest;
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
            @ModelAttribute RoomFilter roomFilter) {
        PaginatedResponse<RoomResponse> result = roomService.filterRooms(page, size, roomFilter);

        return ApiResponse.<List<RoomResponse>>builder()
                .message("Filter users successfully")
                .data(result.getData())
                .meta(result.getMeta())
                .build();
    }

    @PostMapping("/add")
    public ApiResponse<RoomResponse> createRoom(@RequestBody @Valid RoomCreationRequest request) {
        return ApiResponse.<RoomResponse>builder()
                .data(roomService.createRoom(request))
                .message("Add room success")
                .code(201)
                .build();
    }

    @PutMapping("/update/{id}")
    public ApiResponse<RoomResponse> updateRoom(
            @PathVariable("id") String roomId, @RequestBody @Valid RoomUpdateRequest request) {
        return ApiResponse.<RoomResponse>builder()
                .data(roomService.updateRoom(roomId, request))
                .message("Update room success")
                .code(200)
                .build();
    }

    @DeleteMapping("/delete/{id}")
    public ApiResponse<Void> deleteRoom(@PathVariable("id") String roomId) {
        return ApiResponse.<Void>builder()
                .data(roomService.deleteRoom(roomId))
                .message("Delete room success")
                .code(200)
                .build();
    }

    @PutMapping("/update-status/{id}")
    public ApiResponse<RoomResponse> updateRoomStatus(
            @PathVariable("id") String roomId, @RequestParam RoomStatus status) {
        return ApiResponse.<RoomResponse>builder()
                .data(roomService.updateRoomStatus(roomId, status))
                .message("Update room status success")
                .code(200)
                .build();
    }

}
