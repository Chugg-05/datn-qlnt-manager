package com.example.datn_qlnt_manager.service.implement;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.example.datn_qlnt_manager.common.Meta;
import com.example.datn_qlnt_manager.common.Pagination;
import com.example.datn_qlnt_manager.common.VehicleStatus;
import com.example.datn_qlnt_manager.dto.PaginatedResponse;
import com.example.datn_qlnt_manager.dto.filter.VehicleFilter;
import com.example.datn_qlnt_manager.dto.request.vehicle.VehicleCreationRequest;
import com.example.datn_qlnt_manager.dto.request.vehicle.VehicleUpdateRequest;
import com.example.datn_qlnt_manager.dto.response.vehicle.VehicleResponse;
import com.example.datn_qlnt_manager.dto.response.vehicle.VehicleStatisticsResponse;
import com.example.datn_qlnt_manager.entity.Tenant;
import com.example.datn_qlnt_manager.entity.Vehicle;
import com.example.datn_qlnt_manager.exception.AppException;
import com.example.datn_qlnt_manager.exception.ErrorCode;
import com.example.datn_qlnt_manager.mapper.VehicleMapper;
import com.example.datn_qlnt_manager.repository.TenantRepository;
import com.example.datn_qlnt_manager.repository.UserRepository;
import com.example.datn_qlnt_manager.repository.VehicleRepository;
import com.example.datn_qlnt_manager.service.UserService;
import com.example.datn_qlnt_manager.service.VehicleService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class VehicleServiceImpl implements VehicleService {

    VehicleRepository vehicleRepository;
    VehicleMapper vehicleMapper;
    TenantRepository tenantRepository;
    UserRepository userRepository;
    UserService userService;

    @Override
    public PaginatedResponse<VehicleResponse> filterVehicles(VehicleFilter filter, int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(0, page - 1), size, Sort.by(Sort.Direction.DESC, "updatedAt"));
        var user = userService.getCurrentUser();

        if (!userRepository.existsById(user.getId())) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }
        if (tenantRepository.existsById(user.getId())) {
            filter.setTenantId(user.getId());
        } else {
            filter.setUserId(user.getId());
        }

        Page<Vehicle> paging = vehicleRepository.filterVehiclePaging(
                filter.getUserId(), filter.getTenantId(), filter.getVehicleType(), filter.getLicensePlate(), pageable);

        List<VehicleResponse> vehicles = paging.getContent().stream()
                .map(vehicleMapper::toVehicleResponse)
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

        return PaginatedResponse.<VehicleResponse>builder()
                .data(vehicles)
                .meta(meta)
                .build();
    }

    @Override
    public VehicleResponse createVehicle(VehicleCreationRequest request) {
        if (request.getLicensePlate() != null && vehicleRepository.existsByLicensePlate(request.getLicensePlate())) {
            throw new AppException(ErrorCode.LICENSE_PLATE_EXISTED);
        }
        Tenant tenant = tenantRepository
                .findById(request.getTenantId())
                .orElseThrow(() -> new AppException(ErrorCode.VEHICLE_NOT_FOUND));
        Vehicle vehicle = vehicleMapper.toVehicle(request);
        vehicle.setTenant(tenant);
        vehicle.setCreatedAt(Instant.now());
        vehicle.setUpdatedAt(Instant.now());
        return vehicleMapper.toVehicleResponse(vehicleRepository.save(vehicle));
    }

    @Override
    public VehicleResponse updateVehicle(String vehicleId, VehicleUpdateRequest request) {
        Vehicle vehicle =
                vehicleRepository.findById(vehicleId).orElseThrow(() -> new AppException(ErrorCode.VEHICLE_NOT_FOUND));

        vehicleMapper.updateVehicle(vehicle, request);
        vehicle.setUpdatedAt(Instant.now());
        return vehicleMapper.toVehicleResponse(vehicleRepository.save(vehicle));
    }

    @Override
    public void softDeleteVehicleById(String vehicleId) {
        Vehicle vehicle =
                vehicleRepository.findById(vehicleId).orElseThrow(() -> new AppException(ErrorCode.VEHICLE_NOT_FOUND));

        vehicle.setVehicleStatus(VehicleStatus.KHONG_SU_DUNG);
        vehicleMapper.toVehicleResponse(vehicleRepository.save(vehicle));
    }

    @Override
    public void deleteVehicleById(String vehicleId) {
        if (!vehicleRepository.existsById(vehicleId)) {
            throw new AppException(ErrorCode.VEHICLE_NOT_FOUND);
        }
        vehicleRepository.deleteById(vehicleId);
    }

    @Override
    public VehicleStatisticsResponse getVehicleStatistics() {
        var user = userService.getCurrentUser();
        long total = vehicleRepository.countAll(user.getId());
        List<Object[]> countByType = vehicleRepository.countByVehicleType(user.getId());

        Map<String, Long> byType = new HashMap<>();
        for (Object[] row : countByType) {
            String type = row[0].toString();
            Long count = (Long) row[1];
            byType.put(type, count);
        }

        VehicleStatisticsResponse response = new VehicleStatisticsResponse();
        response.setTotal(total);
        response.setByType(byType);
        return response;
    }
}
