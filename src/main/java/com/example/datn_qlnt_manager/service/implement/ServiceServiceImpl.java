package com.example.datn_qlnt_manager.service.implement;

import com.example.datn_qlnt_manager.dto.filter.ServiceFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.example.datn_qlnt_manager.common.Meta;
import com.example.datn_qlnt_manager.common.Pagination;
import com.example.datn_qlnt_manager.common.ServiceStatus;
import com.example.datn_qlnt_manager.dto.PaginatedResponse;
import com.example.datn_qlnt_manager.dto.request.service.ServiceCreationRequest;
import com.example.datn_qlnt_manager.dto.request.service.ServiceUpdateRequest;
import com.example.datn_qlnt_manager.dto.response.service.ServiceResponse;
import com.example.datn_qlnt_manager.entity.Service;
import com.example.datn_qlnt_manager.exception.AppException;
import com.example.datn_qlnt_manager.exception.ErrorCode;
import com.example.datn_qlnt_manager.mapper.ServiceMapper;
import com.example.datn_qlnt_manager.repository.ServiceRepository;
import com.example.datn_qlnt_manager.service.ServiceService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import jakarta.transaction.Transactional;

import java.time.Instant;
import java.util.List;

@org.springframework.stereotype.Service
@Transactional
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ServiceServiceImpl implements ServiceService {

    ServiceRepository serviceRepository;
    ServiceMapper serviceMapper;

    @Override
    public PaginatedResponse<ServiceResponse> getPageAndSearchAndFilterService(ServiceFilter filter, int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(0, page - 1), size, Sort.by(Sort.Order.desc("updatedAt")));

        Page<Service> paging = serviceRepository.filterServicesPaging(
                filter.getQuery(),
                filter.getServiceType(),
                filter.getMinPrice(),
                filter.getMaxPrice(),
                filter.getServiceStatus(),
                filter.getServiceAppliedBy(),
                pageable
        );

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
        Service service = serviceMapper.toServiceCreation(request);

        service.setCreatedAt(Instant.now());
        service.setUpdatedAt(Instant.now());

        return serviceMapper.toServiceResponse(serviceRepository.save(service));
    }

    @Override
    public ServiceResponse updateService(String serviceId, ServiceUpdateRequest request) {
        Service existing = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new AppException(ErrorCode.SERVICE_NOT_FOUND));

        Service updated = serviceMapper.toServiceUpdate(request);
        updated.setId(existing.getId());
        updated.setCreatedAt(existing.getCreatedAt());
        updated.setUpdatedAt(Instant.now());

        return serviceMapper.toServiceResponse(serviceRepository.save(updated));
    }

    @Override
    public Void deleteService(String serviceId) {
        serviceRepository.findById(serviceId)
                .orElseThrow(() -> new AppException(ErrorCode.SERVICE_NOT_FOUND));
        serviceRepository.deleteById(serviceId);
        return null;
    }

    @Override
    public void softDeleteServiceById(String id) {
        Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.SERVICE_NOT_FOUND));
        service.setStatus(ServiceStatus.KHONG_SU_DUNG);
        service.setUpdatedAt(Instant.now());
        serviceRepository.save(service);
    }


}
