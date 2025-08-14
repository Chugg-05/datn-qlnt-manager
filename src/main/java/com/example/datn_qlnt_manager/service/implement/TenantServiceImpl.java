package com.example.datn_qlnt_manager.service.implement;

import java.time.Instant;
import java.util.List;

import com.example.datn_qlnt_manager.dto.response.contract.ContractResponse;
import com.example.datn_qlnt_manager.entity.Room;
import com.example.datn_qlnt_manager.mapper.ContractMapper;
import com.example.datn_qlnt_manager.repository.RoomRepository;
import jakarta.transaction.Transactional;

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
import com.example.datn_qlnt_manager.dto.response.tenant.TenantDetailResponse;
import com.example.datn_qlnt_manager.dto.response.tenant.TenantResponse;
import com.example.datn_qlnt_manager.dto.statistics.TenantStatistics;
import com.example.datn_qlnt_manager.entity.Tenant;
import com.example.datn_qlnt_manager.entity.User;
import com.example.datn_qlnt_manager.exception.AppException;
import com.example.datn_qlnt_manager.exception.ErrorCode;
import com.example.datn_qlnt_manager.mapper.TenantMapper;
import com.example.datn_qlnt_manager.repository.ContractRepository;
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
    RoomRepository roomRepository;
    TenantMapper tenantMapper;
    UserService userService;
    ContractRepository contractRepository;
    CodeGeneratorService codeGeneratorService;
    private final ContractMapper contractMapper;

    @Override
    public PaginatedResponse<TenantResponse> getPageAndSearchAndFilterTenantByUserId(
            TenantFilter filter, int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(0, page - 1), size);
        var user = userService.getCurrentUser();

        Page<Tenant> paging = tenantRepository.getPageAndSearchAndFilterTenantByUserId(
                user.getId(), filter.getQuery(), filter.getGender(), filter.getTenantStatus(), pageable);

        return buildPaginatedTenantResponse(paging, page, size);
    }

    @Override
    public PaginatedResponse<TenantResponse> getTenantWithStatusCancelByUserId(
            TenantFilter filter, int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(0, page - 1), size);
        var user = userService.getCurrentUser();

        Page<Tenant> paging = tenantRepository.getTenantWithStatusCancelByUserId(
                user.getId(), filter.getQuery(), filter.getGender(), pageable);

        return buildPaginatedTenantResponse(paging, page, size);
    }

    @Transactional
    @Override
    public TenantResponse createTenant(TenantCreationRequest request) {
        validateDuplicateTenant(request);

        User owner = userService.getCurrentUser();
        Tenant tenant = tenantMapper.toTenant(request);

        String customerCode = codeGeneratorService.generateTenantCode(owner);

        tenant.setUser(null);
        tenant.setOwner(owner);
        tenant.setCustomerCode(customerCode);
        tenant.setCreatedAt(Instant.now());
        tenant.setUpdatedAt(Instant.now());

        return tenantMapper.toTenantResponse(tenantRepository.save(tenant));
    }

    @Override
    public TenantResponse updateTenant(String tenantId, TenantUpdateRequest request) {
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new AppException(ErrorCode.TENANT_NOT_FOUND));

        if (tenant.getTenantStatus() != TenantStatus.CHO_TAO_HOP_DONG) {
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

        tenant.setUpdatedAt(Instant.now());

        return tenantMapper.toTenantResponse(tenantRepository.save(tenant));
    }

    @Override
    public TenantDetailResponse getTenantDetailById(String tenantId) {
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new AppException(ErrorCode.TENANT_NOT_FOUND));

        long contractCount = tenant.getContractTenants().stream()
                .filter(ct -> Boolean.TRUE.equals(ct.isRepresentative()))
                .count();

        return TenantDetailResponse.builder()
                .id(tenant.getId())
                .customerCode(tenant.getCustomerCode())
                .fullName(tenant.getFullName())
                .phoneNumber(tenant.getPhoneNumber())
                .email(tenant.getEmail())
                .identityCardNumber(tenant.getIdentityCardNumber())
                .dob(tenant.getDob())
                .gender(tenant.getGender())
                .address(tenant.getAddress())
                .tenantStatus(tenant.getTenantStatus())
                .totalContract(contractCount)
                .createdAt(tenant.getCreatedAt())
                .updatedAt(tenant.getUpdatedAt())
                .build();
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

        return tenants.stream()
                .map(tenant -> {
                    List<ContractResponse> contracts = contractRepository.findAllByTenantId(tenant.getId())
                            .stream().map(contractMapper::toContractResponse).toList();
                    TenantResponse tenantResponse = tenantMapper.toTenantResponse(tenant);
                    tenantResponse.setContracts(contracts);
                    return tenantResponse;
                })
                .toList();
    }

    @Override
    public void softDeleteTenantById(String tenantId) {
        Tenant tenant =
                tenantRepository.findById(tenantId).orElseThrow(() -> new AppException(ErrorCode.TENANT_NOT_FOUND));

        if (tenant.getTenantStatus() != TenantStatus.CHO_TAO_HOP_DONG
                && tenant.getTenantStatus() != TenantStatus.DANG_THUE
                && tenant.getTenantStatus() != TenantStatus.DA_TRA_PHONG
        ) {
            throw new AppException(ErrorCode.TENANT_CANNOT_BE_DELETED);
        }

        tenant.setTenantStatus(TenantStatus.HUY_BO);
        tenant.setUpdatedAt(Instant.now());

        tenantRepository.save(tenant);
    }

    @Override
    public void deleteTenantById(String tenantId) {
        Tenant tenant =
                tenantRepository.findById(tenantId).orElseThrow(() -> new AppException(ErrorCode.TENANT_NOT_FOUND));

        if (tenant.getTenantStatus() == TenantStatus.DANG_THUE) {
            throw new AppException(ErrorCode.TENANT_CANNOT_BE_DELETED);
        }

        tenantRepository.deleteById(tenantId);
    }

    @Override
    public List<TenantResponse> getTenantsByRoomId(String roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_FOUND));

        List<Tenant> tenants = tenantRepository.findAllTenantsByRoomId(room.getId());

        return tenants.stream()
                .map(tenantMapper::toTenantResponse)
                .toList();
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

    private PaginatedResponse<TenantResponse> buildPaginatedTenantResponse(Page<Tenant> paging, int page, int size) {

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

}
