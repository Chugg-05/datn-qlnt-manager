package com.example.datn_qlnt_manager.service.implement;

import com.example.datn_qlnt_manager.common.Meta;
import com.example.datn_qlnt_manager.common.Pagination;
import com.example.datn_qlnt_manager.common.UserStatus;
import com.example.datn_qlnt_manager.dto.PaginatedResponse;
import com.example.datn_qlnt_manager.dto.filter.ContractTenantFilter;
import com.example.datn_qlnt_manager.dto.request.ContractTenant.AddTenantToContractRequest;
import com.example.datn_qlnt_manager.dto.request.ContractTenant.RepresentativeChangeRequest;
import com.example.datn_qlnt_manager.dto.response.contractTenant.ContractTenantDetailResponse;
import com.example.datn_qlnt_manager.dto.response.contractTenant.ContractTenantResponse;
import com.example.datn_qlnt_manager.entity.Contract;
import com.example.datn_qlnt_manager.entity.ContractTenant;
import com.example.datn_qlnt_manager.entity.Tenant;
import com.example.datn_qlnt_manager.entity.User;
import com.example.datn_qlnt_manager.exception.AppException;
import com.example.datn_qlnt_manager.exception.ErrorCode;
import com.example.datn_qlnt_manager.repository.ContractRepository;
import com.example.datn_qlnt_manager.repository.ContractTenantRepository;
import com.example.datn_qlnt_manager.repository.TenantRepository;
import com.example.datn_qlnt_manager.repository.UserRepository;
import com.example.datn_qlnt_manager.service.ContractTenantService;
import com.example.datn_qlnt_manager.service.TenantService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ContractTenantServiceImpl implements ContractTenantService {

    ContractTenantRepository contractTenantRepository;
    ContractRepository contractRepository;
    TenantRepository tenantRepository;
    UserRepository userRepository;
    TenantService tenantService;

    @Override
    public PaginatedResponse<ContractTenantDetailResponse> getTenantsFromContract(
            String contractId,
            ContractTenantFilter filter,
            int page,
            int size
    ) {
        Pageable pageable = PageRequest.of(Math.max(0, page - 1), size);

        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new AppException(ErrorCode.CONTRACT_NOT_FOUND));

        Page<ContractTenantDetailResponse> paging = contractTenantRepository.findAllTenantsByContractId(
                contract.getId(),
                filter.getQuery(),
                filter.getGender(),
                pageable);

        List<ContractTenantDetailResponse> tenants = paging.getContent();

        Meta<?> meta = Meta.builder()
                .pagination(Pagination.builder()
                        .count(paging.getNumberOfElements())
                        .perPage(size)
                        .currentPage(page)
                        .totalPages(paging.getTotalPages())
                        .total(paging.getTotalElements())
                        .build())
                .build();

        return PaginatedResponse.<ContractTenantDetailResponse>builder()
                .data(tenants)
                .meta(meta)
                .build();
    }

    @Transactional
    @Override
    public ContractTenantResponse addTenantToContract(AddTenantToContractRequest request) {
        Contract contract = contractRepository.findById(request.getContractId())
                .orElseThrow(() -> new AppException(ErrorCode.CONTRACT_NOT_FOUND));

        Tenant tenant = tenantRepository.findById(request.getTenantId())
                .orElseThrow(() -> new AppException(ErrorCode.TENANT_NOT_FOUND));

        boolean exists = contract.getContractTenants().stream()
                .anyMatch(ct -> ct.getTenant().getId().equals(tenant.getId()));
        if (exists) {
            throw new AppException(ErrorCode.TENANT_ALREADY_IN_CONTRACT);
        }

        ContractTenant contractTenant = ContractTenant.builder()
                .contract(contract)
                .tenant(tenant)
                .representative(false)
                .startDate(LocalDate.now())
                .endDate(contract.getEndDate())
                .build();

        contractTenant.setCreatedAt(Instant.now());
        contractTenant.setUpdatedAt(Instant.now());

        ContractTenant response = contractTenantRepository.save(contractTenant);

        return ContractTenantResponse.builder()
                .id(response.getId())
                .contractId(contract.getId())
                .tenantId(tenant.getId())
                .representative(response.isRepresentative())
                .startDate(response.getStartDate())
                .endDate(response.getEndDate())
                .createdAt(response.getCreatedAt())
                .updatedAt(response.getUpdatedAt())
                .build();
    }

    @Transactional
    @Override
    public void changeRepresentative(RepresentativeChangeRequest request) {
        Contract contract = contractRepository.findById(request.getContractId())
                .orElseThrow(() -> new AppException(ErrorCode.CONTRACT_NOT_FOUND));

        List<ContractTenant> currentReps = contractTenantRepository.findAllByContractIdAndRepresentativeTrue(contract.getId());
        for (ContractTenant rep : currentReps) {
            Tenant oldTenant = rep.getTenant();
            if (oldTenant.getUser() != null) {
                boolean stillRepresentativeElsewhere = contractTenantRepository
                        .existsByTenantIdAndRepresentativeTrueAndContractIdNot(
                                oldTenant.getId(), contract.getId()
                        );

                if (!stillRepresentativeElsewhere) {
                    User oldUser = oldTenant.getUser();
                    oldUser.setUserStatus(UserStatus.LOCKED);
                    oldUser.setUpdatedAt(Instant.now());
                    userRepository.save(oldUser);
                }
            }
            rep.setRepresentative(false);
            rep.setUpdatedAt(Instant.now());
            contractTenantRepository.save(rep);
        }

        Tenant newTenant = tenantRepository.findById(request.getTenantId())
                .orElseThrow(() -> new AppException(ErrorCode.TENANT_NOT_FOUND));

        tenantService.ensureTenantHasActiveUser(newTenant);

        ContractTenant newRep = contractTenantRepository.findByContractIdAndTenantId(contract.getId(), newTenant.getId())
                .orElseThrow(() -> new AppException(ErrorCode.CONTRACT_TENANT_NOT_FOUND));

        newRep.setRepresentative(true);
        newRep.setUpdatedAt(Instant.now());
        contractTenantRepository.save(newRep);
    }

    @Override
    public void deleteTenantFromContract(String contractTenantId) {
        ContractTenant contractTenant = contractTenantRepository.findById(contractTenantId)
                .orElseThrow(() -> new AppException(ErrorCode.CONTRACT_TENANT_NOT_FOUND));

        if (Boolean.TRUE.equals(contractTenant.isRepresentative())) {
            throw new AppException(ErrorCode.CANNOT_DELETE_REPRESENTATIVE_TENANT);
        }

        contractTenantRepository.delete(contractTenant);
    }
}
