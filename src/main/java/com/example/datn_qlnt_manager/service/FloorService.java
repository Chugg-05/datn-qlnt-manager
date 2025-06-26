package com.example.datn_qlnt_manager.service;

import com.example.datn_qlnt_manager.dto.PaginatedResponse;
import com.example.datn_qlnt_manager.dto.filter.FloorFilter;
import com.example.datn_qlnt_manager.dto.request.floor.FloorCreationRequest;
import com.example.datn_qlnt_manager.dto.request.floor.FloorUpdateRequest;
import com.example.datn_qlnt_manager.dto.response.floor.FloorCountResponse;
import com.example.datn_qlnt_manager.dto.response.floor.FloorResponse;

public interface FloorService {

    // thêm
    FloorResponse createFloor(FloorCreationRequest request);

    // hiển thị, lọc, tìm kiếm
    PaginatedResponse<FloorResponse> filterFloors(FloorFilter filter, int page, int size);

    // sửa
    FloorResponse updateFloor(String id, FloorUpdateRequest request);

    // xóa mềm (trạng thái tầng về KHONG_SU_DUNG)
    void softDeleteFloorById(String floorId);

    // xóa luôn
    void deleteFloor(String floorId);

    // thống kê
    FloorCountResponse getFloorCountByBuildingId(String buildingId);
}
