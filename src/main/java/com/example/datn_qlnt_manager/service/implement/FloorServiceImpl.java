package com.example.datn_qlnt_manager.service.implement;

import com.example.datn_qlnt_manager.common.FloorStatus;
import com.example.datn_qlnt_manager.common.Meta;
import com.example.datn_qlnt_manager.common.Pagination;
import com.example.datn_qlnt_manager.dto.PaginatedResponse;
import com.example.datn_qlnt_manager.dto.filter.FloorFilter;
import com.example.datn_qlnt_manager.dto.request.floor.FloorCreationRequest;
import com.example.datn_qlnt_manager.dto.request.floor.FloorUpdateRequest;
import com.example.datn_qlnt_manager.dto.response.floor.FloorResponse;
import com.example.datn_qlnt_manager.entity.Building;
import com.example.datn_qlnt_manager.entity.Floor;
import com.example.datn_qlnt_manager.exception.AppException;
import com.example.datn_qlnt_manager.exception.ErrorCode;
import com.example.datn_qlnt_manager.mapper.FloorMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.example.datn_qlnt_manager.repository.BuildingRepository;
import com.example.datn_qlnt_manager.repository.FloorRepository;
import com.example.datn_qlnt_manager.service.FloorService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.List;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class FloorServiceImpl implements FloorService {

    FloorRepository floorRepository;
    BuildingRepository buildingRepository;
    FloorMapper floorMapper;

    @Override
    public FloorResponse createFloor(FloorCreationRequest request) {

        // Tìm tòa nhà theo ID
        Building building = buildingRepository.findById(request.getBuildingId())
                .orElseThrow(() -> new AppException(ErrorCode.BUILDING_NOT_FOUND));

        // Check trùng tên tầng trong cùng tòa nhà
        floorRepository.findByNameFloorAndBuilding_IdAndIdNot(
                request.getNameFloor(), building.getId(), request.getBuildingId()
        ).ifPresent(f -> {
            throw new AppException(ErrorCode.FLOOR_ALREADY_EXISTS);
        });

        Floor floor = floorMapper.toFloor(request);
        floor.setBuilding(building);
        floor.setCreatedAt(Instant.now());
        floor.setUpdatedAt(Instant.now());

        return floorMapper.toResponse(floorRepository.save(floor));
    }

    @Override
    public PaginatedResponse<FloorResponse> filterFloors(FloorFilter filter, int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(0, page - 1), size, Sort.by("nameFloor").descending());

        Page<Floor> floorPage = floorRepository.filterFloorsPaging(
                filter.getBuildingId(),
                filter.getStatus(),
                filter.getNameFloor(),
                filter.getMaxRoom(),
                pageable
        );

        List<FloorResponse> responses = floorPage.getContent().stream()
                .map(floorMapper::toResponse)
                .toList();

        Meta<?> meta = Meta.builder()
                .pagination(Pagination.builder()
                        .count(floorPage.getNumberOfElements())
                        .perPage(size)
                        .currentPage(page)
                        .totalPages(floorPage.getTotalPages())
                        .total(floorPage.getTotalElements())
                        .build())
                .build();
        return PaginatedResponse.<FloorResponse>builder()
                .data(responses)
                .meta(meta)
                .build();
    }

    @Override
    public FloorResponse updateFloor(String floorId, FloorUpdateRequest request) {
        // Lấy tầng cần cập nhật
        Floor floor = floorRepository.findById(floorId)
                .orElseThrow(() -> new AppException(ErrorCode.FLOOR_NOT_FOUND));

        // Kiểm tra tên tầng trùng trong cùng tòa nhà (trừ chính nó)
        floorRepository.findByNameFloorAndBuilding_Id(request.getNameFloor(), floor.getBuilding().getId())
                .ifPresent(existingFloor -> {
                    if (!existingFloor.getId().equals(floor.getId())) {
                        throw new AppException(ErrorCode.FLOOR_ALREADY_EXISTS);
                    }
                });
        floorMapper.updateFloor(request, floor);
        floor.setUpdatedAt(Instant.now());
        return floorMapper.toResponse(floorRepository.save(floor));
    }

    @Override
    public void softDeleteFloorById(String floorId) { // xóa mềm
        Floor floor = floorRepository.findById(floorId)
                .orElseThrow(() -> new AppException(ErrorCode.FLOOR_NOT_FOUND));
        floor.setStatus(FloorStatus.KHONG_SU_DUNG);
        floorRepository.save(floor);
    }

    @Override
    public void deleteFloor(String floorId) {
        if (!floorRepository.existsById(floorId)) {
            throw new AppException(ErrorCode.FLOOR_NOT_FOUND);
        }
        floorRepository.deleteById(floorId);
    }


}
