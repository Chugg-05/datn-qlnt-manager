package com.example.datn_qlnt_manager.service.implement;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jakarta.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.example.datn_qlnt_manager.common.*;
import com.example.datn_qlnt_manager.dto.PaginatedResponse;
import com.example.datn_qlnt_manager.dto.filter.ServiceFilter;
import com.example.datn_qlnt_manager.dto.request.service.ServiceCreationRequest;
import com.example.datn_qlnt_manager.dto.request.service.ServiceUpdateRequest;
import com.example.datn_qlnt_manager.dto.response.service.ServiceCountResponse;
import com.example.datn_qlnt_manager.dto.response.service.ServiceResponse;
import com.example.datn_qlnt_manager.entity.Service;
import com.example.datn_qlnt_manager.entity.ServicePriceHistory;
import com.example.datn_qlnt_manager.entity.User;
import com.example.datn_qlnt_manager.exception.AppException;
import com.example.datn_qlnt_manager.exception.ErrorCode;
import com.example.datn_qlnt_manager.mapper.ServiceMapper;
import com.example.datn_qlnt_manager.repository.ServicePriceHistoryRepository;
import com.example.datn_qlnt_manager.repository.ServiceRepository;
import com.example.datn_qlnt_manager.service.ServiceService;
import com.example.datn_qlnt_manager.service.UserService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@org.springframework.stereotype.Service
@Transactional
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ServiceServiceImpl implements ServiceService {

    ServiceRepository serviceRepository;
    ServiceMapper serviceMapper;
    UserService userService;
    ServicePriceHistoryRepository servicePriceHistoryRepository;

    @Override
    public PaginatedResponse<ServiceResponse> getPageAndSearchAndFilterService(
            ServiceFilter filter, int page, int size) {
        User user = userService.getCurrentUser();
        Pageable pageable = PageRequest.of(Math.max(0, page - 1), size, Sort.by(Sort.Order.desc("updatedAt")));

        Page<Service> paging = serviceRepository.filterServicesPaging(
                user.getId(),
                filter.getQuery(),
                filter.getServiceCategory(),
                filter.getMinPrice(),
                filter.getMaxPrice(),
                filter.getServiceStatus(),
                filter.getServiceCalculation(),
                pageable);

        return getServiceResponsePaginatedResponse(page, size, paging);
    }

    @Override
    public PaginatedResponse<ServiceResponse> getPageAndSearchAndFilterServiceAndCancel(
            ServiceFilter filter, int page, int size) {
        User user = userService.getCurrentUser();
        Pageable pageable = PageRequest.of(Math.max(0, page - 1), size, Sort.by(Sort.Order.desc("updatedAt")));

        Page<Service> paging = serviceRepository.filterServicesPagingAndCancel(
                user.getId(),
                filter.getQuery(),
                filter.getServiceCategory(),
                filter.getMinPrice(),
                filter.getMaxPrice(),
                filter.getServiceStatus(),
                filter.getServiceCalculation(),
                pageable);

        return getServiceResponsePaginatedResponse(page, size, paging);
    }

    private PaginatedResponse<ServiceResponse> getServiceResponsePaginatedResponse(
            int page, int size, Page<Service> paging) {
        List<ServiceResponse> serviceResponses = paging.getContent().stream()
                .map(serviceMapper::toServiceResponse)
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

        return PaginatedResponse.<ServiceResponse>builder()
                .data(serviceResponses)
                .meta(meta)
                .build();
    }

    @Override
    public ServiceResponse createService(ServiceCreationRequest request) {
        User user = userService.getCurrentUser();

        validateDuplicateCategory(request.getServiceCategory(), user.getId());

        validateCalculationWithCategory(request.getServiceCalculation(), request.getServiceCategory());

        String unit = getDefaultUnit(request.getServiceCalculation(), request.getServiceCategory(), request.getUnit());

        Service service = serviceMapper.toServiceCreation(request);
        service.setUser(user);
        service.setUnit(unit);
        service.setStatus(ServiceStatus.HOAT_DONG);
        Instant now = Instant.now();
        service.setCreatedAt(now);
        service.setUpdatedAt(now);

        return serviceMapper.toServiceResponse(serviceRepository.save(service));
    }

    @Override
    public ServiceResponse updateService(String serviceId, ServiceUpdateRequest request) {
        Service existing =
                serviceRepository.findById(serviceId).orElseThrow(() -> new AppException(ErrorCode.SERVICE_NOT_FOUND));

        // So sánh giá trước và sau
        BigDecimal oldPrice = existing.getPrice();
        BigDecimal newPrice = request.getPrice();

        // Nếu giá thay đổi thì lưu lịch sử
        if (oldPrice != null && !oldPrice.equals(newPrice)) {
            ServicePriceHistory servicePriceHistory = new ServicePriceHistory();
            servicePriceHistory.setService(existing);
            servicePriceHistory.setOldPrice(oldPrice);
            servicePriceHistory.setNewPrice(newPrice);
            servicePriceHistory.setApplicableDate(LocalDateTime.now());
            servicePriceHistoryRepository.save(servicePriceHistory);
        }

        String unit = getDefaultUnit(request.getServiceCalculation(), request.getServiceCategory(), request.getUnit());

        Service updated = serviceMapper.toServiceUpdate(request);
        updated.setId(existing.getId());
        updated.setUnit(unit);
        updated.setCreatedAt(existing.getCreatedAt());
        updated.setUser(existing.getUser());
        updated.setUpdatedAt(Instant.now());
        updated.setStatus(request.getStatus());

        return serviceMapper.toServiceResponse(serviceRepository.save(updated));
    }

    @Override
    public Void deleteService(String serviceId) {
        serviceRepository.findById(serviceId).orElseThrow(() -> new AppException(ErrorCode.SERVICE_NOT_FOUND));
        serviceRepository.deleteById(serviceId);
        return null;
    }

    @Override
    public void softDeleteServiceById(String id) {
        Service service =
                serviceRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.SERVICE_NOT_FOUND));

        service.setPreviousStatus(service.getStatus());
        service.setStatus(ServiceStatus.KHONG_SU_DUNG);
        service.setUpdatedAt(Instant.now());

        serviceRepository.save(service);
    }

    @Override
    public ServiceResponse toggleServiceStatus(String serviceId) {
        Service service = serviceRepository
                .findByIdAndStatusNot(serviceId, ServiceStatus.KHONG_SU_DUNG)
                .orElseThrow(() -> new AppException(ErrorCode.SERVICE_NOT_FOUND));

        if (service.getStatus() == ServiceStatus.HOAT_DONG) {
            service.setStatus(ServiceStatus.TAM_KHOA);
        } else if (service.getStatus() == ServiceStatus.TAM_KHOA) {
            service.setStatus(ServiceStatus.HOAT_DONG);
        } else {
            throw new AppException(ErrorCode.CANNOT_TOGGLE_SERVICE_STATUS);
        }
        service.setUpdatedAt(Instant.now());

        return serviceMapper.toServiceResponse(serviceRepository.save(service));
    }

    @Override
    public ServiceCountResponse statisticsServiceByStatus() {
        return serviceRepository.getServiceStats(userService.getCurrentUser().getId());
    }

    @Override
    public ServiceResponse restoreServiceById(String serviceId) {
        Service service =
                serviceRepository.findById(serviceId).orElseThrow(() -> new AppException(ErrorCode.SERVICE_NOT_FOUND));

        ServiceStatus serviceStatus = service.getStatus();
        ServiceStatus previousStatus = service.getPreviousStatus();

        if (previousStatus != null){
            service.setPreviousStatus(serviceStatus);
            service.setStatus(previousStatus);
        }
        else {
            service.setStatus(ServiceStatus.HOAT_DONG);
        }

        service.setUpdatedAt(Instant.now());
        return serviceMapper.toServiceResponse(serviceRepository.save(service));
    }

    private static final Map<ServiceCalculation, Set<ServiceCategory>> validCategoryMap = Map.of(
            ServiceCalculation.TINH_THEO_SO, Set.of(ServiceCategory.DIEN, ServiceCategory.NUOC),
            ServiceCalculation.TINH_THEO_NGUOI,
                    Set.of(
                            ServiceCategory.NUOC, ServiceCategory.GUI_XE,
                            ServiceCategory.VE_SINH, ServiceCategory.THANG_MAY,
                            ServiceCategory.GIAT_SAY, ServiceCategory.KHAC),
            ServiceCalculation.TINH_THEO_PHONG,
                    Set.of(
                            ServiceCategory.INTERNET,
                            ServiceCategory.VE_SINH,
                            ServiceCategory.THANG_MAY,
                            ServiceCategory.BAO_TRI,
                            ServiceCategory.AN_NINH,
                            ServiceCategory.GIAT_SAY,
                            ServiceCategory.KHAC));

    // validate mối liên hệ giữa cách tính và danh mục
    private void validateCalculationWithCategory(ServiceCalculation calculation, ServiceCategory category) {
        if (category == ServiceCategory.TIEN_PHONG) {
            throw new AppException(ErrorCode.FORBIDDEN_CATEGORY_TYPE);
        }

        Set<ServiceCategory> validCategories = validCategoryMap.get(calculation);
        if (validCategories == null || !validCategories.contains(category)) {
            throw new AppException(ErrorCode.INVALID_CATEGORY_WITH_CALCULATION);
        }
    }

    // Tự set giá trị cho unit dựa theo cách tính
    private String getDefaultUnit(ServiceCalculation calculation, ServiceCategory category, String providedUnit) {

        if (providedUnit != null && !providedUnit.isBlank() && category == ServiceCategory.KHAC) {
            return providedUnit;
        }

        switch (calculation) {
            case TINH_THEO_SO:
                // Phân biệt giữa điện và nước
                if (category == ServiceCategory.DIEN) {
                    return "kWh";
                } else if (category == ServiceCategory.NUOC) {
                    return "m3";
                }
                break;

            case TINH_THEO_NGUOI:
                return "người";

            case TINH_THEO_PHONG:
                return "phòng";

            default:
                throw new AppException(ErrorCode.UNIT_REQUIRED_FOR_CALCULATION);
        }

        throw new AppException(ErrorCode.INVALID_UNIT_COMBINATION);
    }

    // Check trùng danh mục, mỗi danh mục chỉ 1 bản ghi trừ danh mục 'KHAC'
    private void validateDuplicateCategory(ServiceCategory category, String userId) {
        if (category == ServiceCategory.KHAC) {
            return;
        }

        boolean exists = serviceRepository.existsByServiceCategoryAndUserId(category, userId);
        if (exists) {
            throw new AppException(ErrorCode.DUPLICATE_SERVICE_CATEGORY);
        }
    }
}
