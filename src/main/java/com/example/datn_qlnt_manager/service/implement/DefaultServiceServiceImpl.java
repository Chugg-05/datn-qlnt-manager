package com.example.datn_qlnt_manager.service.implement;

import com.example.datn_qlnt_manager.common.BuildingStatus;
import com.example.datn_qlnt_manager.common.DefaultServiceStatus;
import com.example.datn_qlnt_manager.common.Meta;
import com.example.datn_qlnt_manager.common.Pagination;
import com.example.datn_qlnt_manager.dto.PaginatedResponse;
import com.example.datn_qlnt_manager.dto.filter.DefaultServiceFilter;
import com.example.datn_qlnt_manager.dto.request.defaultService.DefaultServiceCreationRequest;
import com.example.datn_qlnt_manager.dto.request.defaultService.DefaultServiceUpdateRequest;
import com.example.datn_qlnt_manager.dto.response.IdAndName;
import com.example.datn_qlnt_manager.dto.response.building.BuildingSelectResponse;
import com.example.datn_qlnt_manager.dto.response.building.DefaultServiceBuildingSelectResponse;
import com.example.datn_qlnt_manager.dto.response.defaultService.DefaultServiceInitResponse;
import com.example.datn_qlnt_manager.dto.response.defaultService.DefaultServiceResponse;
import com.example.datn_qlnt_manager.dto.response.floor.DefaultServiceFloorSelectResponse;
import com.example.datn_qlnt_manager.dto.response.floor.FloorSelectResponse;
import com.example.datn_qlnt_manager.dto.response.room.RoomSelectResponse;
import com.example.datn_qlnt_manager.dto.response.tenant.TenantSelectResponse;
import com.example.datn_qlnt_manager.dto.statistics.DefaultServiceStatistics;
import com.example.datn_qlnt_manager.entity.Building;
import com.example.datn_qlnt_manager.entity.DefaultService;
import com.example.datn_qlnt_manager.entity.Floor;
import com.example.datn_qlnt_manager.entity.User;
import com.example.datn_qlnt_manager.exception.AppException;
import com.example.datn_qlnt_manager.exception.ErrorCode;
import com.example.datn_qlnt_manager.mapper.DefaultServiceMapper;
import com.example.datn_qlnt_manager.repository.BuildingRepository;
import com.example.datn_qlnt_manager.repository.DefaultServiceRepository;
import com.example.datn_qlnt_manager.repository.FloorRepository;
import com.example.datn_qlnt_manager.repository.ServiceRepository;
import com.example.datn_qlnt_manager.service.DefaultServiceService;
import com.example.datn_qlnt_manager.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DefaultServiceServiceImpl implements DefaultServiceService {
    DefaultServiceRepository defaultServiceRepository;
    DefaultServiceMapper defaultServiceMapper;
    ServiceRepository serviceRepository;
    BuildingRepository buildingRepository;
    FloorRepository floorRepository;
    private final UserService userService;


    @Override
    public PaginatedResponse<DefaultServiceResponse> getPageAndSearchAndFilterDefaultServiceByUserId(
            DefaultServiceFilter filter,
            int page,
            int size
    ) {
        Pageable pageable = PageRequest.of(Math.max(0, page - 1), size);

        var user = userService.getCurrentUser();
        Page<DefaultService> paging = defaultServiceRepository.getPageAndSearchAndFilterDefaultServiceByUserId(
                user.getId(),
                filter.getBuildingId(),
                filter.getFloorId(),
                filter.getServiceId(),
                filter.getDefaultServiceStatus(),
                filter.getDefaultServiceAppliesTo(),
                filter.getMaxPricesApply(),
                filter.getMinPricesApply(),
                pageable
        );

        return buildPaginatedDefaultServiceResponse(paging, page, size);
    }

    @Override
    public PaginatedResponse<DefaultServiceResponse> getDefaultServiceWithStatusCancelByUserId(
            DefaultServiceFilter filter,
            int page,
            int size
    ) {
        Pageable pageable = PageRequest.of(Math.max(0, page - 1), size);

        var user = userService.getCurrentUser();
        Page<DefaultService> paging = defaultServiceRepository.getDefaultServiceWithStatusCancelByUserId(
                user.getId(),
                filter.getBuildingId(),
                filter.getFloorId(),
                filter.getServiceId(),
                filter.getDefaultServiceAppliesTo(),
                filter.getMaxPricesApply(),
                filter.getMinPricesApply(),
                pageable
        );

        return buildPaginatedDefaultServiceResponse(paging, page, size);
    }

    @Override
    public DefaultServiceResponse createDefaultService(DefaultServiceCreationRequest request) {

        com.example.datn_qlnt_manager.entity.Service service = serviceRepository.findById(request.getServiceId())
                .orElseThrow(() -> new AppException(ErrorCode.SERVICE_NOT_FOUND));
        Building building = buildingRepository.findById(request.getBuildingId())
                .orElseThrow(() -> new AppException(ErrorCode.BUILDING_NOT_FOUND));
        Floor floor = floorRepository.findById(request.getFloorId())
                .orElseThrow(() -> new AppException(ErrorCode.FLOOR_NOT_FOUND));
        if (defaultServiceRepository.existsByBuildingIdAndServiceIdAndDefaultServiceAppliesTo(
                request.getBuildingId(),
                request.getServiceId(),
                request.getDefaultServiceAppliesTo()
        )) {
            throw new AppException(ErrorCode.DUPLICATE_SERVICE);
        }


        DefaultService defaultService = defaultServiceMapper.toDefaultService(request);
        if (request.getPricesApply() == null) {
            defaultService.setPricesApply(service.getPrice());
        }
        defaultService.setService(service);
        defaultService.setBuilding(building);
        defaultService.setFloor(floor);
        defaultService.setDefaultServiceStatus(DefaultServiceStatus.HOAT_DONG);
        defaultService.setCreatedAt(Instant.now());
        defaultService.setUpdatedAt(Instant.now());
        return defaultServiceMapper.toDefaultServiceResponse(defaultServiceRepository.save(defaultService));
    }

    @Override
    public DefaultServiceResponse updateDefaultService(String defaultServiceId, DefaultServiceUpdateRequest request) {
        DefaultService defaultService = defaultServiceRepository.findById(defaultServiceId)
                .orElseThrow(() -> new AppException(ErrorCode.DEFAULT_SERVICE_NOT_FOUND));

        defaultServiceMapper.updateDefaultService(defaultService, request);
        defaultService.setUpdatedAt(Instant.now());
        return defaultServiceMapper.toDefaultServiceResponse(defaultServiceRepository.save(defaultService));
    }

    @Override
    public void deleteDefaultServiceById(String defaultServiceId) {
        if (!defaultServiceRepository.existsById(defaultServiceId)) {
            throw new AppException(ErrorCode.DEFAULT_SERVICE_NOT_FOUND);
        }
        defaultServiceRepository.deleteById(defaultServiceId);
    }

    @Override
    public DefaultServiceInitResponse initDefaultService() {
        User user = userService.getCurrentUser();

        List<IdAndName> services = serviceRepository.findAllByUserId(user.getId()).stream().toList();

        List<DefaultServiceBuildingSelectResponse> buildings = buildingRepository.findAllBuildingsByUserId(user.getId())
                .stream()
                .map(b -> {
                    List<DefaultServiceFloorSelectResponse> floorSelectResponses =
                            floorRepository.findAllFloorsByUserIdAndBuildingId(user.getId(), b.getId()).stream()
                                    .map(f -> new DefaultServiceFloorSelectResponse(f.getId(), f.getName()))
                                    .toList();
                    return DefaultServiceBuildingSelectResponse.builder()
                            .id(b.getId())
                            .name(b.getName())
                            .floors(floorSelectResponses)
                            .build();
                })
                .toList();

        return DefaultServiceInitResponse.builder()
                .services(services)
                .buildings(buildings)
                .build();
    }

    @Override
    public void softDeleteDefaultServiceById(String defaultServiceId) {
        DefaultService defaultService = defaultServiceRepository
                .findById(defaultServiceId)
                .orElseThrow(() -> new AppException(ErrorCode.DEFAULT_SERVICE_NOT_FOUND));
        defaultService.setDefaultServiceStatus(DefaultServiceStatus.HUY_BO);
        defaultService.setUpdatedAt(Instant.now());
        defaultServiceRepository.save(defaultService);
    }

    @Override
    public void toggleStatus(String defaultServiceId) {
        DefaultService defaultService = defaultServiceRepository
                .findByIdAndDefaultServiceStatusNot(defaultServiceId, DefaultServiceStatus.HUY_BO)
                .orElseThrow(() -> new AppException(ErrorCode.DEFAULT_SERVICE_NOT_FOUND));

        if (defaultService.getDefaultServiceStatus() == DefaultServiceStatus.HOAT_DONG) {
            defaultService.setDefaultServiceStatus(DefaultServiceStatus.TAM_DUNG);
        } else if (defaultService.getDefaultServiceStatus() == DefaultServiceStatus.TAM_DUNG) {
            defaultService.setDefaultServiceStatus(DefaultServiceStatus.HOAT_DONG);
        } else {
            throw new IllegalStateException("Cannot toggle status for deleted default service");
        }
        defaultService.setUpdatedAt(Instant.now());
        defaultServiceRepository.save(defaultService);
    }

    @Override
    public DefaultServiceStatistics statisticsDefaultServiceByStatus() {
        User user = userService.getCurrentUser();
        return defaultServiceRepository.statisticsDefaultServiceByStatusWhereUserId(user.getId());
    }

    private PaginatedResponse<DefaultServiceResponse> buildPaginatedDefaultServiceResponse(
            Page<DefaultService> paging, int page, int size) {

        List<DefaultServiceResponse> defaultServices = paging.getContent().stream()
                .map(defaultServiceMapper::toDefaultServiceResponse)
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

        return PaginatedResponse.<DefaultServiceResponse>builder()
                .data(defaultServices)
                .meta(meta)
                .build();
    }

}
