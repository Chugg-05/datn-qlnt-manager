package com.example.datn_qlnt_manager.service;

import com.example.datn_qlnt_manager.dto.PaginatedResponse;
import com.example.datn_qlnt_manager.dto.filter.FloorFilter;
import com.example.datn_qlnt_manager.dto.request.floor.FloorCreationRequest;
import com.example.datn_qlnt_manager.dto.request.floor.FloorUpdateRequest;
import com.example.datn_qlnt_manager.dto.response.IdAndName;
import com.example.datn_qlnt_manager.dto.response.floor.FloorBasicResponse;
import com.example.datn_qlnt_manager.dto.response.floor.FloorResponse;
import com.example.datn_qlnt_manager.dto.statistics.FloorStatistics;

import java.util.List;

public interface FloorService {

    // thêm
    FloorResponse createFloor(FloorCreationRequest request);

    // hiển thị, lọc, tìm kiếm
    PaginatedResponse<FloorResponse> getPageAndSearchAndFilterFloorByUserId(FloorFilter filter, int page, int size);

    PaginatedResponse<FloorResponse> getTenantWithStatusCancelByUserId(
            FloorFilter filter,
            int page,
            int size
    );

    // sửa
    FloorResponse updateFloor(String id, FloorUpdateRequest request);

    // xóa mềm (trạng thái tầng về KHONG_SU_DUNG)
    void softDeleteFloorById(String floorId);

    // xóa luôn
    void deleteFloor(String floorId);

    // thống kê
    FloorStatistics getFloorCountByBuildingId(String buildingId);

    // hiển thị ầng theo userId và buildingId
//    List<FloorBasicResponse> getFloorBasicByUserIdAndBuildingId(String userId, String buildingId);

    // hiển thị tầng theo building id
    List<FloorBasicResponse> getFloorBasicByBuildingId(String buildingId);
    void toggleStatus(String id);

    List<IdAndName> getFloorsByUserId();
}
