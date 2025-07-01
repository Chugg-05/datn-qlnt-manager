package com.example.datn_qlnt_manager.service.implement;

import com.example.datn_qlnt_manager.common.DefaultServiceStatus;
import com.example.datn_qlnt_manager.common.Meta;
import com.example.datn_qlnt_manager.common.Pagination;
import com.example.datn_qlnt_manager.dto.PaginatedResponse;
import com.example.datn_qlnt_manager.dto.filter.DefaultServiceFilter;
import com.example.datn_qlnt_manager.dto.request.defaultService.DefaultServiceCreationRequest;
import com.example.datn_qlnt_manager.dto.request.defaultService.DefaultServiceUpdateRequest;
import com.example.datn_qlnt_manager.dto.response.defaultService.DefaultServiceResponse;
import com.example.datn_qlnt_manager.entity.Building;
import com.example.datn_qlnt_manager.entity.DefaultService;
import com.example.datn_qlnt_manager.entity.Floor;
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
    public PaginatedResponse<DefaultServiceResponse> filterDefaultServices(DefaultServiceFilter filter, int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(0, page - 1), size);

        var user = userService.getCurrentUser();
        Page<DefaultService> paging = defaultServiceRepository.filterDefaultServicePaging(
                user.getId(), filter.getBuildingId(), filter.getFloorId(), filter.getServiceId(), filter.getDefaultServiceStatus(), filter.getDefaultServiceAppliesTo(), filter.getMaxPricesApply(), filter.getMinPricesApply(), pageable
        );

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
        if (request.getPricesApply() == null){
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

}
