package com.example.datn_qlnt_manager.service;

import com.example.datn_qlnt_manager.dto.ApiResponse;
import com.example.datn_qlnt_manager.dto.PaginatedResponse;
import com.example.datn_qlnt_manager.dto.filter.FloorFilter;

import com.example.datn_qlnt_manager.dto.request.floor.FloorCreationRequest;
import com.example.datn_qlnt_manager.dto.request.floor.FloorUpdateRequest;
import com.example.datn_qlnt_manager.dto.response.floor.FloorResponse;



public interface FloorService {

    //thêm
    ApiResponse<FloorResponse> createFloor(FloorCreationRequest request);

    // hiển thị, lọc, tìm kiếm
    PaginatedResponse<FloorResponse> filterFloors(FloorFilter filter, int page, int size);

    // xóa mềm (trạng thái tầng về KHONG_SU_DUNG)
    void softDeleteFloorById(String floorId);

    //sửa
    ApiResponse<FloorResponse> updateFloor(String id, FloorUpdateRequest request);

    // xóa luôn
    ApiResponse<Void> deleteFloor(String floorId);
}
