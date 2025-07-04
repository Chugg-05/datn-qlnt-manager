package com.example.datn_qlnt_manager.service.implement;

import java.time.Instant;
import java.util.List;

import com.example.datn_qlnt_manager.common.TenantStatus;
import com.example.datn_qlnt_manager.dto.response.tenant.TenantDetailResponse;
import com.example.datn_qlnt_manager.dto.statistics.TenantStatistics;
import com.example.datn_qlnt_manager.repository.ContractRepository;
import com.example.datn_qlnt_manager.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.datn_qlnt_manager.common.Meta;
import com.example.datn_qlnt_manager.common.Pagination;
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
    UserRepository userRepository;
    ContractRepository contractRepository;
    CodeGeneratorService codeGeneratorService;

    @Override
    public PaginatedResponse<TenantResponse> getPageAndSearchAndFilterTenantByUserId(TenantFilter filter, int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(0, page - 1), size);
        var user = userService.getCurrentUser();

        Page<Tenant> paging = tenantRepository.getPageAndSearchAndFilterTenantByUserId(
                user.getId(),
                filter.getQuery(),
                filter.getGender(),
                filter.getTenantStatus(),
                pageable);

        return buildPaginatedTenantResponse(paging, page, size);
    }

    @Override
    public PaginatedResponse<TenantResponse> getTenantWithStatusCancelByUserId(TenantFilter filter, int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(0, page - 1), size);
        var user = userService.getCurrentUser();

        Page<Tenant> paging = tenantRepository.getTenantWithStatusCancelByUserId(
                user.getId(),
                filter.getQuery(),
                filter.getGender(),
                pageable);

        return buildPaginatedTenantResponse(paging, page, size);
    }

    @Transactional
    @Override
    public TenantResponse createTenantByOwner(TenantCreationRequest request) {
        validateDuplicateTenant(request);

        User owner = userService.getCurrentUser();
        Tenant tenant = tenantMapper.toTenant(request);
        String customerCode = codeGeneratorService.generateTenantCode(owner);

        tenant.setOwner(owner);
        tenant.setCustomerCode(customerCode);
        tenant.setHasAccount(true);
        tenant.setIsRepresentative(true);
        tenant.setUser(userService.createUserForTenant(request));
        tenant.setCreatedAt(Instant.now());
        tenant.setUpdatedAt(Instant.now());

        return tenantMapper.toTenantResponse(tenantRepository.save(tenant));
    }

    @Transactional
    @Override
    public TenantResponse createTenantByRepresentative(TenantCreationRequest request) {
        validateDuplicateTenant(request);

        User creator = userService.getCurrentUser();

        Tenant representative = tenantRepository.findByUserId(creator.getId())
                .orElseThrow(() -> new AppException(ErrorCode.TENANT_NOT_FOUND));

        User owner = representative.getOwner();

        Tenant tenant = tenantMapper.toTenant(request);
        String customerCode = codeGeneratorService.generateTenantCode(owner);

        tenant.setUser(null);
        tenant.setOwner(owner);
        tenant.setCustomerCode(customerCode);
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

        if (contractRepository.existsByTenants_Id(tenantId)) {
            throw new AppException(ErrorCode.TENANT_ALREADY_IN_CONTRACT);
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

        if (tenant.getUser() != null) {
            User user = tenant.getUser();
            user.setFullName(request.getFullName());
            user.setDob(request.getDob());
            user.setGender(request.getGender());
            user.setPhoneNumber(request.getPhoneNumber());
            user.setUpdatedAt(Instant.now());
            userRepository.save(user);
        }

        tenant.setUpdatedAt(Instant.now());

        return tenantMapper.toTenantResponse(tenantRepository.save(tenant));
    }

    @Override
    public TenantDetailResponse getTenantDetailById(String tenantId) {
        return tenantRepository.findTenantDetailById(tenantId)
                .orElseThrow(() -> new AppException(ErrorCode.TENANT_NOT_FOUND));
    }

    @Override
    public void toggleTenantStatusById(String tenantId) {
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new AppException(ErrorCode.TENANT_NOT_FOUND));

        if (tenant.getTenantStatus() == TenantStatus.DANG_THUE) {
            tenant.setTenantStatus(TenantStatus.DA_TRA_PHONG);
        } else if (tenant.getTenantStatus() == TenantStatus.DA_TRA_PHONG) {
            tenant.setTenantStatus(TenantStatus.DANG_THUE);
        } else {
            throw new AppException(ErrorCode.TENANT_CANNOT_BE_TOGGLED);
        }

        tenant.setUpdatedAt(Instant.now());

        tenantRepository.save(tenant);
    }

    @Override
    public TenantStatistics getTenantStatisticsByUserId() {
        User user = userService.getCurrentUser();

        return tenantRepository.getTotalTenantByStatus(user.getId());
    }

    @Override
    public List<TenantResponse> getAllTenantsByUserId() {
        User user = userService.getCurrentUser();
        List<Tenant> tenants = tenantRepository.findAllTenantsByUserId(user.getId());
        return tenants.stream().map(tenantMapper::toTenantResponse).toList();
    }

    @Override
    public void softDeleteTenantById(String tenantId) {
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new AppException(ErrorCode.TENANT_NOT_FOUND));

        if (tenant.getTenantStatus() != TenantStatus.DANG_THUE && tenant.getTenantStatus() != TenantStatus.DA_TRA_PHONG) {
            throw new AppException(ErrorCode.TENANT_CANNOT_BE_DELETED);
        }

        tenant.setTenantStatus(TenantStatus.HUY_BO);
        tenant.setUpdatedAt(Instant.now());

        tenantRepository.save(tenant);
    }

    @Override
    public void deleteTenantById(String tenantId) {
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new AppException(ErrorCode.TENANT_NOT_FOUND));

        if (tenant.getTenantStatus() == TenantStatus.DANG_THUE) {
            throw new AppException(ErrorCode.TENANT_CANNOT_BE_DELETED);
        }

        tenantRepository.deleteById(tenantId);
    }

    private void validateDuplicateTenant(TenantCreationRequest request) {
        if (tenantRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_EXISTED);
        }
        if (tenantRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new AppException(ErrorCode.PHONE_NUMBER_EXISTED);
        }
        if (tenantRepository.existsByIdentityCardNumber(request.getIdentityCardNumber())) {
            throw new AppException(ErrorCode.ID_NUMBER_EXISTED);
        }
    }

    private PaginatedResponse<TenantResponse> buildPaginatedTenantResponse(
            Page<Tenant> paging, int page, int size) {

        List<TenantResponse> tenants = paging.getContent()
                .stream()
                .map(tenantMapper::toTenantResponse)
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

        return PaginatedResponse.<TenantResponse>builder()
                .data(tenants)
                .meta(meta)
                .build();
    }
}
