package com.example.datn_qlnt_manager.controller;

import java.util.List;
import java.util.UUID;

import com.example.datn_qlnt_manager.common.RoomStatus;
import com.example.datn_qlnt_manager.dto.request.filter.RoomFilter;
import com.example.datn_qlnt_manager.dto.request.room.RoomCreationRequest;
import com.example.datn_qlnt_manager.dto.request.room.RoomDeleteRequest;
import com.example.datn_qlnt_manager.dto.request.room.RoomUpdateRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import com.example.datn_qlnt_manager.dto.ApiResponse;
import com.example.datn_qlnt_manager.dto.response.room.RoomResponse;
import com.example.datn_qlnt_manager.service.RoomService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/rooms")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoomController {

    RoomService roomService;

    @GetMapping
    public ApiResponse<List<RoomResponse>> findAll(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "15") Integer size,
            @ModelAttribute RoomFilter roomFilter){


        return roomService.findAll(
                page, size, roomFilter);
    }

    @PostMapping("/add")
    public ApiResponse<RoomResponse> createRoom(@RequestBody @Valid RoomCreationRequest request){
        return ApiResponse.<RoomResponse>builder()
                .data(roomService.createRoom(request))
                .message("Add room success")
                .code(201)
                .build();
    }

    @PutMapping("/update/{id}")
    public ApiResponse<RoomResponse> updateRoom(
            @PathVariable("id") UUID roomId,
            @RequestBody @Valid RoomUpdateRequest request) {
        return ApiResponse.<RoomResponse>builder()
                .data(roomService.updateRoom(roomId, request))
                .message("Update room success")
                .code(200)
                .build();
    }

    @DeleteMapping("/delete/{id}")
    public ApiResponse<RoomResponse> deleteRoom(@PathVariable("id") UUID roomId,
                                                @RequestBody @Valid RoomDeleteRequest request) {
        return ApiResponse.<RoomResponse>builder()
                .data(roomService.deleteRoom(roomId, request))
                .message("Delete room success")
                .code(200)
                .build();
    }


    @PutMapping("/update-status/{id}")
    public ApiResponse<RoomResponse> updateRoomStatus(
            @PathVariable("id") UUID roomId,
            @RequestParam RoomStatus status) {
        return ApiResponse.<RoomResponse>builder()
                .data(roomService.updateRoomStatus(roomId, status))
                .message("Update room status success")
                .code(200)
                .build();
    }



}
