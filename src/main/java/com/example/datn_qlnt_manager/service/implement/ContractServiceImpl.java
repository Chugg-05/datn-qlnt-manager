package com.example.datn_qlnt_manager.service.implement;

import com.example.datn_qlnt_manager.common.ContractStatus;
import com.example.datn_qlnt_manager.common.Meta;
import com.example.datn_qlnt_manager.common.Pagination;
import com.example.datn_qlnt_manager.dto.PaginatedResponse;
import com.example.datn_qlnt_manager.dto.filter.ContractFilter;
import com.example.datn_qlnt_manager.dto.request.contract.ContractCreationRequest;
import com.example.datn_qlnt_manager.dto.request.contract.ContractUpdateRequest;
import com.example.datn_qlnt_manager.dto.response.contract.ContractDetailResponse;
import com.example.datn_qlnt_manager.dto.response.contract.ContractResponse;
import com.example.datn_qlnt_manager.dto.response.tenant.TenantBasicResponse;
import com.example.datn_qlnt_manager.dto.statistics.ContractStatistics;
import com.example.datn_qlnt_manager.entity.Contract;
import com.example.datn_qlnt_manager.entity.Room;
import com.example.datn_qlnt_manager.entity.Tenant;
import com.example.datn_qlnt_manager.exception.AppException;
import com.example.datn_qlnt_manager.exception.ErrorCode;
import com.example.datn_qlnt_manager.mapper.ContractMapper;
import com.example.datn_qlnt_manager.repository.ContractRepository;
import com.example.datn_qlnt_manager.repository.RoomRepository;
import com.example.datn_qlnt_manager.repository.TenantRepository;
import com.example.datn_qlnt_manager.service.ContractService;
import com.example.datn_qlnt_manager.service.UserService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ContractServiceImpl implements ContractService {
    RoomRepository roomRepository;
    ContractRepository contractRepository;
    TenantRepository tenantRepository;
    CodeGeneratorService codeGeneratorService;
    ContractMapper contractMapper;
    UserService userService;

    @Override
    public PaginatedResponse<ContractResponse> filterContracts(ContractFilter filter, int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(0, page - 1), size);
        var user = userService.getCurrentUser();
        filter.setUserId(user.getId());

        Page<Contract> paging = contractRepository.filterContractPaging(
                filter.getUserId(),
                filter.getQuery(),
                filter.getGender(),
                filter.getStatus(),
                pageable
        );

        List<ContractResponse> contracts = paging.getContent().stream()
                .map(contractMapper::toContractResponse)
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


        return PaginatedResponse.<ContractResponse>builder()
                .data(contracts)
                .meta(meta)
                .build();
    }

    @Transactional
    @Override
    public ContractResponse createContract(ContractCreationRequest request) {
        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_FOUND));

        boolean existsContract = contractRepository.existsByRoomIdAndStatusIn(
                room.getId(),
                List.of(ContractStatus.HIEU_LUC, ContractStatus.SAP_HET_HAN)
        );

        if (existsContract) {
            throw new AppException(ErrorCode.ROOM_ALREADY_HAS_CONTRACT);
        }

        if (request.getNumberOfPeople() > room.getMaximumPeople()) {
            throw new AppException(ErrorCode.NUMBER_OF_PEOPLE_EXCEEDS_LIMIT);
        }

        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new AppException(ErrorCode.END_DATE_BEFORE_START_DATE);
        }

        Set<Tenant> tenants = new HashSet<>(tenantRepository.findAllById(request.getTenants()));
        if (tenants.size() != request.getTenants().size()) {
            throw new AppException(ErrorCode.TENANT_NOT_FOUND);
        }

        if (request.getTenants().size() > request.getNumberOfPeople()) {
            throw new AppException(ErrorCode.TENANTS_EXCEEDS_NUMBER_OF_PEOPLE);
        }

        String contractCode = codeGeneratorService.generateContractCode(room);

        Contract contract = contractMapper.toContract(request);
        contract.setRoom(room);
        contract.setTenants(tenants);
        contract.setContractCode(contractCode);
        contract.setCreatedAt(Instant.now());
        contract.setUpdatedAt(Instant.now());

        contractRepository.save(contract);

        return contractMapper.toContractResponse(contract);
    }

    @Override
    public ContractResponse updateContract(String contractId, ContractUpdateRequest request) {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new AppException(ErrorCode.CONTRACT_NOT_FOUND));

        if (request.getEndDate().isBefore(contract.getStartDate())) {
            throw new AppException(ErrorCode.END_DATE_BEFORE_START_DATE);
        }

        if (request.getNumberOfPeople() > contract.getRoom().getMaximumPeople()) {
            throw new AppException(ErrorCode.NUMBER_OF_PEOPLE_EXCEEDS_LIMIT);
        }

        if (request.getTenants().size() > request.getNumberOfPeople()) {
            throw new AppException(ErrorCode.TENANTS_EXCEEDS_NUMBER_OF_PEOPLE);
        }

        Set<Tenant> tenants = new HashSet<>(tenantRepository.findAllById(request.getTenants()));
        if (tenants.size() != request.getTenants().size()) {
            throw new AppException(ErrorCode.TENANT_NOT_FOUND);
        }

        contractMapper.updateContract(request, contract);

        contract.setTenants(tenants);
        contract.setUpdatedAt(Instant.now());

        contractRepository.save(contract);

        return contractMapper.toContractResponse(contract);
    }

    @Override
    public List<ContractResponse> getAllContractsByUserId(String userId) {
        List<Contract> contracts = contractRepository.findAllContractByUserId(userId);
        return contracts.stream()
                .map(contractMapper::toContractResponse)
                .toList();
    }

    @Override
    public void toggleContractStatusById(String contractId) {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new AppException(ErrorCode.CONTRACT_NOT_FOUND));

        if (contract.getStatus() == ContractStatus.HIEU_LUC || contract.getStatus() == ContractStatus.SAP_HET_HAN) {
            contract.setStatus(ContractStatus.DA_HUY);

        } else if (contract.getStatus() == ContractStatus.DA_HUY) {
            if (contract.getEndDate() != null && contract.getEndDate().isBefore(LocalDateTime.now())) {
                throw new AppException(ErrorCode.CANNOT_REACTIVATE_EXPIRED_CONTRACT);
            }

            contract.setStatus(ContractStatus.HIEU_LUC);

        } else {
            throw new AppException(ErrorCode.CANNOT_TOGGLE_CONTRACT_STATUS);
        }

        contract.setUpdatedAt(Instant.now());

        contractRepository.save(contract);
    }

    @Override
    public ContractDetailResponse getContractDetailById(String contractId) {
        ContractDetailResponse detail = contractRepository.findContractDetailById(contractId)
                .orElseThrow(() -> new AppException(ErrorCode.CONTRACT_NOT_FOUND));

        List<TenantBasicResponse> tenants = tenantRepository.findTenantsByContractId(contractId);
        detail.setTenants(new HashSet<>(tenants));

        return detail;
    }

    @Override
    public ContractStatistics getContractStatisticsByUserId() {
        var user = userService.getCurrentUser();

        return contractRepository.getTotalContractByStatus(user.getId());
    }

    @Override
    public void softDeleteContractById(String contractId) {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new AppException(ErrorCode.CONTRACT_NOT_FOUND));

        if (contract.getStatus() == ContractStatus.HIEU_LUC || contract.getStatus() == ContractStatus.SAP_HET_HAN) {
            throw new AppException(ErrorCode.CANNOT_DELETE_CONTRACT);
        }

        contract.setStatus(ContractStatus.DA_HUY);
        contract.setUpdatedAt(Instant.now());

        contractRepository.save(contract);
    }


    @Override
    public void deleteContractById(String contractId) {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new AppException(ErrorCode.CONTRACT_NOT_FOUND));

        if (contract.getStatus() == ContractStatus.HIEU_LUC || contract.getStatus() == ContractStatus.SAP_HET_HAN) {
            throw new AppException(ErrorCode.CANNOT_DELETE_CONTRACT);
        }

        contractRepository.delete(contract);
    }

}
