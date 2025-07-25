package com.example.datn_qlnt_manager.service.implement;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.datn_qlnt_manager.common.Meta;
import com.example.datn_qlnt_manager.common.Pagination;
import com.example.datn_qlnt_manager.common.VehicleStatus;
import com.example.datn_qlnt_manager.common.VehicleType;
import com.example.datn_qlnt_manager.dto.PaginatedResponse;
import com.example.datn_qlnt_manager.dto.filter.VehicleFilter;
import com.example.datn_qlnt_manager.dto.request.vehicle.VehicleCreationRequest;
import com.example.datn_qlnt_manager.dto.request.vehicle.VehicleUpdateRequest;
import com.example.datn_qlnt_manager.dto.response.vehicle.VehicleResponse;
import com.example.datn_qlnt_manager.dto.statistics.VehicleStatistics;
import com.example.datn_qlnt_manager.entity.Tenant;
import com.example.datn_qlnt_manager.entity.Vehicle;
import com.example.datn_qlnt_manager.exception.AppException;
import com.example.datn_qlnt_manager.exception.ErrorCode;
import com.example.datn_qlnt_manager.mapper.VehicleMapper;
import com.example.datn_qlnt_manager.repository.TenantRepository;
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
    UserService userService;

    @Override
    public PaginatedResponse<VehicleResponse> getPageAndSearchAndFilterVehicleByUserId(
            VehicleFilter filter, int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(0, page - 1), size);
        var user = userService.getCurrentUser();

        Page<Vehicle> paging = vehicleRepository.getPageAndSearchAndFilterVehicleByUserId(
                user.getId(), filter.getVehicleType(), filter.getLicensePlate(), pageable);

        return buildPaginatedVehicleResponse(paging, page, size);
    }

    @Override
    public PaginatedResponse<VehicleResponse> getVehicleWithStatusCancelByUserId(
            VehicleFilter filter, int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(0, page - 1), size);
        var user = userService.getCurrentUser();

        Page<Vehicle> paging = vehicleRepository.getVehicleWithStatusCancelByUserId(
                user.getId(), filter.getVehicleType(), filter.getLicensePlate(), pageable);

        return buildPaginatedVehicleResponse(paging, page, size);
    }

    @Override
    public VehicleResponse createVehicle(VehicleCreationRequest request) {
        if (request.getLicensePlate() != null && vehicleRepository.existsByLicensePlate(request.getLicensePlate())) {
            throw new AppException(ErrorCode.LICENSE_PLATE_EXISTED);
        }

        if ((request.getLicensePlate() == null || request.getLicensePlate().isBlank())
                && request.getVehicleType() == VehicleType.XE_DAP) {
            request.setLicensePlate(null);
        } else {
            if (!isValidCivilianLicensePlate(request.getLicensePlate())) {
                throw new AppException(ErrorCode.INVALID_LICENSE_PLATE);
            }
        }

        Tenant tenant = tenantRepository
                .findById(request.getTenantId())
                .orElseThrow(() -> new AppException(ErrorCode.TENANT_NOT_FOUND));
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

    private static final Map<String, String> TYPE_MAPPING = Map.of(
            "XE_MAY", "motorbike",
            "OTO", "car",
            "XE_DAP", "bicycle",
            "KHAC", "other");

    @Override
    public VehicleStatistics getVehicleStatistics() {
        var user = userService.getCurrentUser();
        long total = vehicleRepository.countAll(user.getId());

        Map<String, Long> result = new LinkedHashMap<>();
        for (String camelKey : TYPE_MAPPING.values()) {
            result.put(camelKey, 0L);
        }

        List<Object[]> typeCounts = vehicleRepository.countByVehicleType(user.getId());
        for (Object[] row : typeCounts) {
            String dbType = row[0].toString();
            Long count = (Long) row[1];

            String camelKey = TYPE_MAPPING.get(dbType);
            if (camelKey != null) {
                result.put(camelKey, count);
            }
        }

        return new VehicleStatistics(total, result);
    }

    @Override
    public void toggleStatus(String id) {
        Vehicle vehicle = vehicleRepository
                .findByIdAndVehicleStatusNot(id, VehicleStatus.TAM_KHOA)
                .orElseThrow(() -> new AppException(ErrorCode.VEHICLE_NOT_FOUND));

        if (vehicle.getVehicleStatus() == VehicleStatus.SU_DUNG) {
            vehicle.setVehicleStatus(VehicleStatus.TAM_KHOA);
            vehicle.setUpdatedAt(Instant.now());
        } else if (vehicle.getVehicleStatus() == VehicleStatus.TAM_KHOA) {
            vehicle.setVehicleStatus(VehicleStatus.SU_DUNG);
            vehicle.setUpdatedAt(Instant.now());
        } else {
            throw new IllegalStateException("Cannot toggle status for deleted vehicle");
        }
        vehicleRepository.save(vehicle);
    }

    private PaginatedResponse<VehicleResponse> buildPaginatedVehicleResponse(Page<Vehicle> paging, int page, int size) {

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

    // check biển số xe
    private static final String LICENSE_PLATE_REGEX = "^(0[1-9]|[1-9][0-9])[A-Z]{1,2}[0-9]?-?[0-9]{4,6}$";

    private static final String[] INVALID_PREFIXES = {
        "NG", "QT", "CD", "LD", "HC", "DA", "R", "TĐ", "MK", "MD", "MĐ", "XN", "TD"
    };

    public boolean isValidCivilianLicensePlate(String plate) {
        if (plate == null || plate.isBlank()) {
            throw new AppException(ErrorCode.INVALID_LICENSE_PLATE_BLANK);
        }

        String normalized = plate.trim().toUpperCase();

        for (String prefix : INVALID_PREFIXES) {
            if (normalized.startsWith(prefix)) {
                return false;
            }
        }
        return normalized.matches(LICENSE_PLATE_REGEX);
    }
}
