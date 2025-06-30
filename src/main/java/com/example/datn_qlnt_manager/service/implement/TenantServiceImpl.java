package com.example.datn_qlnt_manager.service.implement;

import java.time.Instant;
import java.util.List;

import com.example.datn_qlnt_manager.dto.statistics.TenantStatistics;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.datn_qlnt_manager.common.Meta;
import com.example.datn_qlnt_manager.common.Pagination;
import com.example.datn_qlnt_manager.common.TenantStatus;
import com.example.datn_qlnt_manager.dto.PaginatedResponse;
import com.example.datn_qlnt_manager.dto.filter.TenantFilter;
import com.example.datn_qlnt_manager.dto.request.tenant.TenantCreationRequest;
import com.example.datn_qlnt_manager.dto.request.tenant.TenantUpdateRequest;
import com.example.datn_qlnt_manager.dto.response.tenant.TenantResponse;
import com.example.datn_qlnt_manager.entity.Tenant;
import com.example.datn_qlnt_manager.entity.User;
import com.example.datn_qlnt_manager.exception.AppException;
import com.example.datn_qlnt_manager.exception.ErrorCode;
import com.example.datn_qlnt_manager.mapper.TenantMapper;
import com.example.datn_qlnt_manager.repository.TenantRepository;
import com.example.datn_qlnt_manager.service.TenantService;
import com.example.datn_qlnt_manager.service.UserService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TenantServiceImpl implements TenantService {
    TenantRepository tenantRepository;
    TenantMapper tenantMapper;
    UserService userService;
    CodeGeneratorService codeGeneratorService;

    @Override
    public PaginatedResponse<TenantResponse> filterTenants(TenantFilter filter, int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(0, page - 1), size);
        var user = userService.getCurrentUser();
        filter.setUserId(user.getId());

        Page<Tenant> paging = tenantRepository.filterTenantPaging(
                filter.getUserId(),
                filter.getCustomerCode(),
                filter.getFullName(),
                filter.getEmail(),
                filter.getPhoneNumber(),
                filter.getIdentityCardNumber(),
                filter.getAddress(),
                filter.getGender(),
                filter.getTenantStatus(),
                pageable);

        List<TenantResponse> tenants =
                paging.getContent().stream().map(tenantMapper::toTenantResponse).toList();

        Meta<?> meta = Meta.builder()
                .pagination(Pagination.builder()
                        .count(paging.getNumberOfElements())
                        .perPage(size)
                        .currentPage(page)
                        .totalPages(paging.getTotalPages())
                        .total(paging.getTotalElements())
                        .build())
                .build();

        return PaginatedResponse.<TenantResponse>builder()
                .data(tenants)
                .meta(meta)
                .build();
    }

    @Override
    public TenantResponse createTenant(TenantCreationRequest request) {
        if (tenantRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_EXISTED);
        }

        if (tenantRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new AppException(ErrorCode.PHONE_NUMBER_EXISTED);
        }

        if (tenantRepository.existsByIdentityCardNumber(request.getIdentityCardNumber())) {
            throw new AppException(ErrorCode.ID_NUMBER_EXISTED);
        }

        Tenant tenant = tenantMapper.toTenant(request);

        User user = userService.getCurrentUser();

        Building building = buildingRepository
                .findByUserId(user.getId())
                .orElseThrow(() -> new AppException(ErrorCode.BUILDING_NOT_FOUND));

        String customerCode = codeGeneratorService.generateTenantCode(building);

        tenant.setUser(user);
        tenant.setCustomerCode(customerCode);
        tenant.setTenantStatus(TenantStatus.DANG_THUE);
        tenant.setIsRepresentative(false);
        tenant.setHasAccount(false);
        tenant.setCreatedAt(Instant.now());
        tenant.setUpdatedAt(Instant.now());

        return tenantMapper.toTenantResponse(tenantRepository.save(tenant));
    }

    @Override
    public TenantResponse updateTenant(String tenantId, TenantUpdateRequest request) {
        Tenant tenant =
                tenantRepository.findById(tenantId).orElseThrow(() -> new AppException(ErrorCode.TENANT_NOT_FOUND));

        if (!tenant.getEmail().equals(request.getEmail()) && tenantRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_EXISTED);
        }

        if (!tenant.getPhoneNumber().equals(request.getPhoneNumber())
                && tenantRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new AppException(ErrorCode.PHONE_NUMBER_EXISTED);
        }

        if (!tenant.getIdentityCardNumber().equals(request.getIdentityCardNumber())
                && tenantRepository.existsByIdentityCardNumber(request.getIdentityCardNumber())) {
            throw new AppException(ErrorCode.ID_NUMBER_EXISTED);
        }

        tenantMapper.updateTenant(request, tenant);
        tenant.setUpdatedAt(Instant.now());

        return tenantMapper.toTenantResponse(tenantRepository.save(tenant));
    }

    @Override
    public TenantStatistics totalTenantsByStatus() {
        User user = userService.getCurrentUser();

        return tenantRepository.getTotalTenantByStatus(user.getId());
    }

    @Override
    public TenantResponse getTenantById(String tenantId) {
        Tenant tenant =
                tenantRepository.findById(tenantId).orElseThrow(() -> new AppException(ErrorCode.TENANT_NOT_FOUND));
        return tenantMapper.toTenantResponse(tenant);
    }

    @Override
    public void deleteTenantById(String tenantId) {
        if (!tenantRepository.existsById(tenantId)) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }
        tenantRepository.deleteById(tenantId);
    }
}
