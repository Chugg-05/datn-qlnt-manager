package com.example.datn_qlnt_manager.service.implement;

import java.time.Instant;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import com.example.datn_qlnt_manager.dto.request.ContractTenant.ContractTenantCreationRequest;
import com.example.datn_qlnt_manager.dto.request.contract.ContractExtendRequest;
import com.example.datn_qlnt_manager.dto.request.contract.TerminateContractRequest;
import com.example.datn_qlnt_manager.dto.response.asset.AssetLittleResponse;
import com.example.datn_qlnt_manager.dto.response.service.ServiceLittleResponse;
import com.example.datn_qlnt_manager.dto.response.tenant.TenantLittleResponse;
import com.example.datn_qlnt_manager.dto.response.vehicle.VehicleBasicResponse;
import com.example.datn_qlnt_manager.service.DepositService;
import com.example.datn_qlnt_manager.service.TenantService;
import jakarta.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.datn_qlnt_manager.common.*;
import com.example.datn_qlnt_manager.dto.PaginatedResponse;
import com.example.datn_qlnt_manager.dto.filter.ContractFilter;
import com.example.datn_qlnt_manager.dto.request.contract.ContractCreationRequest;
import com.example.datn_qlnt_manager.dto.request.contract.ContractUpdateRequest;
import com.example.datn_qlnt_manager.dto.response.contract.ContractDetailResponse;
import com.example.datn_qlnt_manager.dto.response.contract.ContractResponse;
import com.example.datn_qlnt_manager.dto.statistics.ContractStatistics;
import com.example.datn_qlnt_manager.entity.*;
import com.example.datn_qlnt_manager.exception.AppException;
import com.example.datn_qlnt_manager.exception.ErrorCode;
import com.example.datn_qlnt_manager.mapper.ContractMapper;
import com.example.datn_qlnt_manager.repository.*;
import com.example.datn_qlnt_manager.service.ContractService;
import com.example.datn_qlnt_manager.service.UserService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ContractServiceImpl implements ContractService {
    RoomRepository roomRepository;
    ContractRepository contractRepository;
    TenantRepository tenantRepository;
    ContractTenantRepository contractTenantRepository;
    ContractVehicleRepository contractVehicleRepository;
    AssetRoomRepository assetRoomRepository;
    ServiceRoomRepository serviceRoomRepository;
    VehicleRepository vehicleRepository;
    ContractMapper contractMapper;
    CodeGeneratorService codeGeneratorService;
    UserService userService;
    TenantService tenantService;
    DepositService depositService;

    @Override
    public PaginatedResponse<ContractResponse> getPageAndSearchAndFilterTenantByUserId(
            ContractFilter filter, int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(0, page - 1), size);
        var user = userService.getCurrentUser();

        Page<Contract> paging = contractRepository.getPageAndSearchAndFilterContractByUserId(
                user.getId(),
                filter.getQuery(),
                filter.getBuilding(),
                filter.getGender(),
                filter.getStatus(),
                pageable);

        return buildPaginatedContractResponse(paging, page, size);
    }

    @Override
    public PaginatedResponse<ContractResponse> getContractWithStatusCancelByUserId(
            ContractFilter filter, int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(0, page - 1), size);
        var user = userService.getCurrentUser();

        Page<Contract> paging = contractRepository.getContractWithStatusCancelByUserId(
                user.getId(),
                filter.getQuery(),
                filter.getBuilding(),
                filter.getGender(),
                pageable);

        return buildPaginatedContractResponse(paging, page, size);
    }

    @Transactional
    @Override
    public ContractResponse createContract(ContractCreationRequest request) {
        Room room = validateCreateContractRequest(request);

        String contractCode = codeGeneratorService.generateContractCode(room);

        Contract contract = contractMapper.toContract(request);

        contract.setContractCode(contractCode);
        contract.setRoom(room);
        contract.setRoomPrice(room.getPrice());
        applyUtilityPrices(contract);
        contract.setContent(request.getContent());

        contract.setCreatedAt(Instant.now());
        contract.setUpdatedAt(Instant.now());

        contractRepository.save(contract);

        room.setStatus(RoomStatus.DANG_THUE);
        room.setUpdatedAt(Instant.now());

        roomRepository.save(room);

        Set<ContractTenant> contractTenants = request.getTenants().stream()
                .map(ctRequest -> {
                    Tenant tenant = tenantRepository.findById(ctRequest.getTenantId())
                            .orElseThrow(() -> new AppException(ErrorCode.TENANT_NOT_FOUND));

                    if (tenant.getTenantStatus() == TenantStatus.KHOA || tenant.getTenantStatus() == TenantStatus.HUY_BO) {
                        throw new AppException(ErrorCode.TENANT_NOT_ELIGIBLE_FOR_CONTRACT);
                    }

                    if (tenant.getTenantStatus() != TenantStatus.DANG_THUE) {
                        tenant.setTenantStatus(TenantStatus.DANG_THUE);
                        tenant.setUpdatedAt(Instant.now());
                        tenantRepository.save(tenant);
                    }

                    if (Boolean.TRUE.equals(ctRequest.getRepresentative())) {
                        tenantService.ensureTenantHasActiveUser(tenant);
                    }

                    ContractTenant contractTenant = ContractTenant.builder()
                            .contract(contract)
                            .tenant(tenant)
                            .representative(ctRequest.getRepresentative())
                            .startDate(contract.getStartDate())
                            .endDate(contract.getEndDate())
                            .build();

                    contractTenant.setCreatedAt(Instant.now());
                    contractTenant.setUpdatedAt(Instant.now());

                    return contractTenant;

                })
                .collect(Collectors.toSet());

        if (!contractTenants.isEmpty()) {
            contractTenantRepository.saveAll(contractTenants);
        }

        if (request.getVehicles() != null && !request.getVehicles().isEmpty()) {
            List<Vehicle> vehicles = vehicleRepository.findAllById(request.getVehicles());
            if (vehicles.size() != request.getVehicles().size()) {
                throw new AppException(ErrorCode.VEHICLE_NOT_FOUND);
            }
            List<ContractVehicle> contractVehicles = vehicles.stream()
                    .map(v -> {
                        ContractVehicle contractVehicle = ContractVehicle.builder()
                                .contract(contract)
                                .vehicle(v)
                                .startDate(contract.getStartDate())
                                .endDate(contract.getEndDate())
                                .build();

                        contractVehicle.setCreatedAt(Instant.now());
                        contractVehicle.setUpdatedAt(Instant.now());

                        return contractVehicle;
                    })
                    .toList();

            contractVehicleRepository.saveAll(contractVehicles);
        }
        var res = contractMapper.toContractResponse(contract);
        res.setContent(contract.getContent());
        return res;
    }

    @Transactional
    @Override
    public ContractResponse updateContract(String contractId, ContractUpdateRequest request) {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new AppException(ErrorCode.CONTRACT_NOT_FOUND));

        validateContractUpdate(contract, request);

        contractMapper.updateContract(request, contract);

        contract.setRoomPrice(contract.getRoom().getPrice());
        contract.setUpdatedAt(Instant.now());

        return contractMapper.toContractResponse(contractRepository.save(contract));
    }

    @Override
    public ContractDetailResponse getContractDetail(String contractId) {
        ContractDetailResponse detail = contractRepository
                .findContractDetailById(contractId)
                .orElseThrow(() -> new AppException(ErrorCode.CONTRACT_NOT_FOUND));

        List<TenantLittleResponse> tenants = contractTenantRepository.findAllTenantLittleResponseByContractId(contractId);
        List<AssetLittleResponse> assets = assetRoomRepository.findAllAssetLittleResponseByRoomId(detail.getRoomId());
        List<ServiceLittleResponse> services = serviceRoomRepository.findAllServiceLittleResponseByRoomId(detail.getRoomId());
        List<VehicleBasicResponse> vehicles = contractVehicleRepository.findAllVehicleBasicResponseByContractId(contractId);

        detail.setServices(services);
        detail.setAssets(assets);
        detail.setTenants(tenants);
        detail.setVehicles(vehicles);
        detail.setContent(detail.getContent());

        return detail;
    }

    @Override
    public List<ContractResponse> getAllContractsByUserId() {
        User user = userService.getCurrentUser();
        List<Contract> contracts = contractRepository.findAllContractByUserId(user.getId());
        return contracts.stream().map(contractMapper::toContractResponse).toList();
    }


    @Override
    public void contractActivation(String contractId) {
        Contract contract = contractRepository
                .findById(contractId)
                .orElseThrow(() -> new AppException(ErrorCode.CONTRACT_NOT_FOUND));

        if (contract.getStatus() != ContractStatus.CHO_KICH_HOAT) {
            throw new AppException(ErrorCode.CANNOT_ACTIVATION_CONTRACT);
        }

        contract.setStatus(ContractStatus.HIEU_LUC);

        if (contract.getStartDate().isAfter(LocalDate.now())) {
            contract.setStartDate(LocalDate.now());
        }

        contract.setUpdatedAt(Instant.now());

        depositService.createDepositForContract(contract);

        contractRepository.save(contract);
    }

    @Override
    public ContractStatistics getContractStatisticsByUserId() {
        var user = userService.getCurrentUser();

        return contractRepository.getTotalContractByStatus(user.getId());
    }

    @Override
    public void softDeleteContractById(String contractId) {
        Contract contract = contractRepository
                .findById(contractId)
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
        Contract contract = contractRepository
                .findById(contractId)
                .orElseThrow(() -> new AppException(ErrorCode.CONTRACT_NOT_FOUND));

        if (contract.getStatus() == ContractStatus.HIEU_LUC || contract.getStatus() == ContractStatus.SAP_HET_HAN) {
            throw new AppException(ErrorCode.CANNOT_DELETE_CONTRACT);
        }

        contractRepository.delete(contract);
    }

    @Override
    public PaginatedResponse<ContractResponse> getContractsOfCurrentTenant(ContractFilter filter, int page, int size) {
        String userId = userService.getCurrentUser().getId();
        Pageable pageable = PageRequest.of(Math.max(0, page - 1), size);

        Page<Contract> paging = contractRepository.getPageAndSearchAndFilterContractByTenantUserId(
                userId,
                filter.getQuery(),
                filter.getGender(),
                filter.getStatus(),
                pageable
        );
        return buildPaginatedContractResponse(paging, page, size);
    }

    private void applyUtilityPrices(Contract contract) {
        Room room = contract.getRoom();
        if (room != null && room.getServiceRooms() != null) {
            for (ServiceRoom serviceRoom : room.getServiceRooms()) {
                com.example.datn_qlnt_manager.entity.Service service = serviceRoom.getService();
                if (service != null) {
                    if (service.getServiceCategory() == ServiceCategory.DIEN) {
                        contract.setElectricPrice(service.getPrice());
                    } else if (service.getServiceCategory() == ServiceCategory.NUOC) {
                        contract.setWaterPrice(service.getPrice());
                    }
                }
            }
        }
    }

    private Room validateCreateContractRequest(ContractCreationRequest request) {
        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_FOUND));

        if (room.getStatus() != RoomStatus.TRONG) {
            throw new AppException(ErrorCode.ROOM_NOT_AVAILABLE);
        }

        int requestedTenantCount = request.getTenants().size();
        if (requestedTenantCount > room.getMaximumPeople()) {
            throw new AppException(ErrorCode.NUMBER_OF_PEOPLE_EXCEEDS_LIMIT);
        }

        if (!request.getStartDate().isBefore(request.getEndDate())) {
            throw new AppException(ErrorCode.END_DATE_BEFORE_START_DATE);
        }

        boolean hasActiveContract = contractRepository.existsByRoomIdAndEndDateAfter(
                request.getRoomId(),
                request.getStartDate()
        );
        if (hasActiveContract) {
            throw new AppException(ErrorCode.ROOM_ALREADY_IN_CONTRACT);
        }

        Set<String> tenantIds = request.getTenants().stream()
                .map(ContractTenantCreationRequest::getTenantId)
                .collect(Collectors.toSet());
        if (tenantIds.size() != requestedTenantCount) {
            throw new AppException(ErrorCode.DUPLICATED_TENANTS_IN_REQUEST);
        }

        long representativeCount = request.getTenants().stream()
                .filter(t -> Boolean.TRUE.equals(t.getRepresentative()))
                .count();
        if (representativeCount != 1) {
            throw new AppException(ErrorCode.INVALID_REPRESENTATIVE_SELECTION);
        }

        if (!assetRoomRepository.existsByRoomId(room.getId())) {
            throw new AppException(ErrorCode.ROOM_HAS_NO_ASSET);
        }

        if (request.getVehicles() != null && !request.getVehicles().isEmpty()) {
            List<Vehicle> inUseVehicles = vehicleRepository.findActiveVehiclesInContracts(
                    request.getVehicles(),
                    request.getStartDate()
            );
            if (!inUseVehicles.isEmpty()) {
                throw new AppException(ErrorCode.VEHICLE_ALREADY_IN_ACTIVE_CONTRACT);
            }
        }

        return room;
    }

    @Override
    public ContractResponse restoreContractById(String contractId) {
        Contract contract = contractRepository
                .findById(contractId)
                .orElseThrow(() -> new AppException(ErrorCode.CONTRACT_NOT_FOUND));
        contract.setStatus(ContractStatus.HIEU_LUC);
        return null;
    }


    @Override
    public String updateContent(String contractId, String content) {
        Contract contract = contractRepository
                .findById(contractId)
                .orElseThrow(() -> new AppException(ErrorCode.CONTRACT_NOT_FOUND));
        contract.setContent(content);
        return contractRepository.save(contract).getContent();
    }

    // gia hạn
    @Override
    public ContractResponse extendContract(String contractId, ContractExtendRequest request) {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new AppException(ErrorCode.CONTRACT_NOT_FOUND));

        if (contract.getStatus() != ContractStatus.HIEU_LUC && contract.getStatus() != ContractStatus.SAP_HET_HAN) {
            throw new AppException(ErrorCode.CONTRACT_NOT_ELIGIBLE_FOR_EXTENSION);
        }

        if (request.getNewEndDate().isBefore(contract.getEndDate())
                || request.getNewEndDate().isEqual(contract.getEndDate())) {
            throw new AppException(ErrorCode.END_DATE_BEFORE_CURRENT);
        }

        contract.setEndDate(request.getNewEndDate());
        contract.setUpdatedAt(Instant.now());

        contractRepository.save(contract);

        return contractMapper.toContractResponse(contract);
    }

    // báo trước
    @Override
    public ContractResponse terminateContractWithNotice(String contractId, TerminateContractRequest request) {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new AppException(ErrorCode.CONTRACT_NOT_FOUND));

        if (!ContractStatus.HIEU_LUC.equals(contract.getStatus())) {
            throw new AppException(ErrorCode.CONTRACT_NOT_ACTIVE);
        }

        if (request.getNewEndDate().isAfter(contract.getEndDate())) {
            throw new AppException(ErrorCode.INVALID_END_DATE);
        }

        contract.setStatus(ContractStatus.KET_THUC_CO_BAO_TRUOC);
        contract.setEndDate(request.getNewEndDate());
        contract.setUpdatedAt(Instant.now());

        Contract saved = contractRepository.save(contract);
        return contractMapper.toContractResponse(saved);
    }

    // tự ý bỏ đi
    @Override
    public ContractResponse forceCancelContract(String contractId) {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new AppException(ErrorCode.CONTRACT_NOT_FOUND));

        if (!ContractStatus.HIEU_LUC.equals(contract.getStatus())) {
            throw new AppException(ErrorCode.CONTRACT_NOT_ACTIVE);
        }

        contract.setStatus(ContractStatus.TU_Y_HUY_BO);
        contract.setEndDate(LocalDate.now());
        contract.setUpdatedAt(Instant.now());

        contractRepository.save(contract);

        return contractMapper.toContractResponse(contract);
    }

    private void validateContractUpdate(Contract contract, ContractUpdateRequest request) {
        if (!contract.getStartDate().isBefore(request.getEndDate())) {
            throw new AppException(ErrorCode.END_DATE_BEFORE_START_DATE);
        }

        if (contract.getContractTenants().size() > contract.getRoom().getMaximumPeople()) {
            throw new AppException(ErrorCode.NUMBER_OF_PEOPLE_EXCEEDS_LIMIT);
        }

        Set<String> tenantIds = contract.getContractTenants().stream()
                .map(ct -> ct.getTenant().getId())
                .collect(Collectors.toSet());

        if (tenantIds.size() != contract.getContractTenants().size()) {
            throw new AppException(ErrorCode.DUPLICATED_TENANTS_IN_REQUEST);
        }

        long representativeCount = contract.getContractTenants().stream()
                .filter(ct -> Boolean.TRUE.equals(ct.isRepresentative()))
                .count();
        if (representativeCount != 1) {
            throw new AppException(ErrorCode.INVALID_REPRESENTATIVE_SELECTION);
        }

        if (!assetRoomRepository.existsByRoomId(contract.getRoom().getId())) {
            throw new AppException(ErrorCode.ROOM_HAS_NO_ASSET);
        }

        Set<String> vehicleIds = contract.getContractVehicles().stream()
                .map(cv -> cv.getVehicle().getId())
                .collect(Collectors.toSet());

        if (!vehicleIds.isEmpty()) {
            List<Vehicle> inUseVehicles = vehicleRepository.findActiveVehiclesInOtherContracts(
                    vehicleIds,
                    contract.getId(),
                    contract.getStartDate()
            );

            if (!inUseVehicles.isEmpty()) {
                throw new AppException(ErrorCode.VEHICLE_ALREADY_IN_ACTIVE_CONTRACT);
            }
        }
    }

    private PaginatedResponse<ContractResponse> buildPaginatedContractResponse(
            Page<Contract> paging, int page, int size) {

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
}
