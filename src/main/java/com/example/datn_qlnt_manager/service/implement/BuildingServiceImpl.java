package com.example.datn_qlnt_manager.service.implement;

import java.time.Instant;
import java.util.List;

import com.example.datn_qlnt_manager.dto.response.building.BuildingBasicResponse;
import com.example.datn_qlnt_manager.dto.statistics.BuildingStatistics;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.example.datn_qlnt_manager.common.BuildingStatus;
import com.example.datn_qlnt_manager.common.Meta;
import com.example.datn_qlnt_manager.common.Pagination;
import com.example.datn_qlnt_manager.dto.PaginatedResponse;
import com.example.datn_qlnt_manager.dto.filter.BuildingFilter;
import com.example.datn_qlnt_manager.dto.request.building.BuildingCreationRequest;
import com.example.datn_qlnt_manager.dto.request.building.BuildingUpdateRequest;
import com.example.datn_qlnt_manager.dto.response.building.BuildingResponse;
import com.example.datn_qlnt_manager.entity.Building;
import com.example.datn_qlnt_manager.exception.AppException;
import com.example.datn_qlnt_manager.exception.ErrorCode;
import com.example.datn_qlnt_manager.mapper.BuildingMapper;
import com.example.datn_qlnt_manager.repository.BuildingRepository;
import com.example.datn_qlnt_manager.service.BuildingService;
import com.example.datn_qlnt_manager.service.UserService;
import com.example.datn_qlnt_manager.utils.CodeGeneratorUtil;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BuildingServiceImpl implements BuildingService {

    BuildingRepository buildingRepository;
    BuildingMapper buildingMapper;
    UserService userService;

    @Override
    public PaginatedResponse<BuildingResponse> filterBuildings(BuildingFilter filter, int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(0, page - 1), size, Sort.by(Sort.Direction.DESC, "updatedAt"));
        var user = userService.getCurrentUser();
        filter.setUserId(user.getId());

        Page<Building> paging = buildingRepository.filterBuildingPaging(
                filter.getUserId(), filter.getQuery(), filter.getStatus(), filter.getBuildingType(), pageable);

        List<BuildingResponse> buildings = paging.getContent().stream()
                .map(buildingMapper::toBuildingResponse)
                .toList();

        Meta<?> meta = Meta.builder()
                .pagination(Pagination.builder()
                        .count(paging.getNumberOfElements())
                        .perPage(size)
                        .currentPage(page)
                        .totalPages(paging.getTotalPages())
                        .total(paging.getTotalElements())
                        .build())
                .build();
        return PaginatedResponse.<BuildingResponse>builder()
                .data(buildings)
                .meta(meta)
                .build();
    }

    @Override
    public List<BuildingBasicResponse> getBuildingBasicForCurrentUser(){
        var user = userService.getCurrentUser();
        return buildingRepository.findAllBuildingBasicByUserId(user.getId());
    }

    @Override
    public BuildingResponse createBuilding(BuildingCreationRequest request) {
        var user = userService.getCurrentUser();
        String code = CodeGeneratorUtil.generateSecureCode("TOA");

        if (buildingRepository.existsByBuildingNameAndUserId(request.getBuildingName(), user.getId())) {
            throw new AppException(ErrorCode.BUILDING_NAME_EXISTED);
        }
        if (request.getActualNumberOfFloors() < request.getNumberOfFloorsForRent()) {
            throw new AppException(ErrorCode.INVALID_FLOORS_NUMBER_FOR_RENT);
        }

        Building building = buildingMapper.toBuilding(request);
        building.setUser(user);
        building.setBuildingCode(code);
        building.setStatus(BuildingStatus.HOAT_DONG);
        building.setCreatedAt(Instant.now());
        building.setUpdatedAt(Instant.now());

        return buildingMapper.toBuildingResponse(buildingRepository.save(building));
    }

    @Override
    public BuildingResponse updateBuilding(String buildingId, BuildingUpdateRequest request) {
        if (request.getActualNumberOfFloors() < request.getNumberOfFloorsForRent()) {
            throw new AppException(ErrorCode.INVALID_FLOORS_NUMBER_FOR_RENT);
        }

        Building building = buildingRepository
                .findById(buildingId)
                .orElseThrow(() -> new AppException(ErrorCode.BUILDING_NOT_FOUND));

        String userId = building.getUser().getId();
        boolean exists =
                buildingRepository.existsByBuildingNameAndUserIdAndIdNot(request.getBuildingName(), userId, buildingId);
        if (exists) {
            throw new AppException(ErrorCode.BUILDING_NAME_EXISTED);
        }

        buildingMapper.updateBuilding(building, request);

        building.setUpdatedAt(Instant.now());

        return buildingMapper.toBuildingResponse(buildingRepository.save(building));
    }

    @Override
    public void softDeleteBuildingById(String buildingId) {
        Building building = buildingRepository
                .findById(buildingId)
                .orElseThrow(() -> new AppException(ErrorCode.BUILDING_NOT_FOUND));
        building.setStatus(BuildingStatus.HUY_HOAT_DONG);
        buildingMapper.toBuildingResponse(buildingRepository.save(building));
    }

    @Override
    public void deleteBuildingById(String buildingId) {
        if (!buildingRepository.existsById(buildingId)) {
            throw new AppException(ErrorCode.BUILDING_NOT_FOUND);
        }
        buildingRepository.deleteById(buildingId);
    }

    @Override
    public BuildingStatistics statisticsBuildingByStatus() {
        var user = userService.getCurrentUser();
        return buildingRepository.getBuildingStatsByUser(user.getId());
    }

    @Override
    public void toggleStatus(String id) {
        Building building = buildingRepository
                .findByIdAndStatusNot(id, BuildingStatus.HUY_HOAT_DONG)
                .orElseThrow(() -> new AppException(ErrorCode.BUILDING_NOT_FOUND));

        if (building.getStatus() == BuildingStatus.HOAT_DONG) {
            building.setStatus(BuildingStatus.TAM_KHOA);
            building.setUpdatedAt(Instant.now());
        } else if (building.getStatus() == BuildingStatus.TAM_KHOA) {
            building.setStatus(BuildingStatus.HOAT_DONG);
            building.setUpdatedAt(Instant.now());
        } else {
            throw new IllegalStateException("Cannot toggle status for deleted building");
        }
        buildingRepository.save(building);
    }
}
