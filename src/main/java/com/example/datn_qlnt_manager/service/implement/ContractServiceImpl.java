package com.example.datn_qlnt_manager.service.implement;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
import com.example.datn_qlnt_manager.dto.response.asset.AssetBasicResponse;
import com.example.datn_qlnt_manager.dto.response.contract.ContractDetailResponse;
import com.example.datn_qlnt_manager.dto.response.contract.ContractResponse;
import com.example.datn_qlnt_manager.dto.response.service.ServiceBasicResponse;
import com.example.datn_qlnt_manager.dto.response.tenant.TenantBasicResponse;
import com.example.datn_qlnt_manager.dto.response.vehicle.VehicleBasicResponse;
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
    CodeGeneratorService codeGeneratorService;
    ContractMapper contractMapper;
    UserService userService;
    AssetRepository assetRepository;
    ServiceRepository serviceRepository;
    VehicleRepository vehicleRepository;

    @Override
    public PaginatedResponse<ContractResponse> getPageAndSearchAndFilterTenantByUserId(
            ContractFilter filter, int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(0, page - 1), size);
        var user = userService.getCurrentUser();

        Page<Contract> paging = contractRepository.getPageAndSearchAndFilterContractByUserId(
                user.getId(), filter.getQuery(), filter.getGender(), filter.getStatus(), pageable);

        return buildPaginatedContractResponse(paging, page, size);
    }

    @Override
    public PaginatedResponse<ContractResponse> getContractWithStatusCancelByUserId(
            ContractFilter filter, int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(0, page - 1), size);
        var user = userService.getCurrentUser();

        Page<Contract> paging = contractRepository.getContractWithStatusCancelByUserId(
                user.getId(), filter.getQuery(), filter.getGender(), pageable);

        return buildPaginatedContractResponse(paging, page, size);
    }

    @Transactional
    @Override
    public ContractResponse createContract(ContractCreationRequest request) {
        Room room = roomRepository
                .findById(request.getRoomId())
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_FOUND));

        if (contractRepository.existsByRoomIdAndStatusIn(
                room.getId(), List.of(ContractStatus.HIEU_LUC, ContractStatus.SAP_HET_HAN))) {
            throw new AppException(ErrorCode.ROOM_ALREADY_HAS_CONTRACT);
        }

        validateRoomForContract(room, request);

        Set<Tenant> tenants = new HashSet<>(tenantRepository.findAllById(request.getTenants()));
        if (tenants.size() != request.getTenants().size()) {
            throw new AppException(ErrorCode.TENANT_NOT_FOUND);
        }

        Set<Asset> assets = request.getAssets() != null && !request.getAssets().isEmpty()
                ? new HashSet<>(assetRepository.findAllById(request.getAssets()))
                : new HashSet<>();
        if (assets.size() != request.getAssets().size()) {
            throw new AppException(ErrorCode.ASSET_NOT_FOUND);
        }

        Set<com.example.datn_qlnt_manager.entity.Service> services =
                request.getServices() != null && !request.getServices().isEmpty()
                        ? new HashSet<>(serviceRepository.findAllById(request.getServices()))
                        : new HashSet<>();
        if (services.size() != request.getServices().size()) {
            throw new AppException(ErrorCode.SERVICE_NOT_FOUND);
        }

        Set<Vehicle> vehicles =
                request.getVehicles() != null && !request.getVehicles().isEmpty()
                        ? new HashSet<>(vehicleRepository.findAllById(request.getVehicles()))
                        : new HashSet<>();

        if (vehicles.size() != request.getVehicles().size()) {
            throw new AppException(ErrorCode.VEHICLE_NOT_FOUND);
        }

        if (request.getTenants().size() != request.getNumberOfPeople()) {
            throw new AppException(ErrorCode.TENANTS_EXCEEDS_NUMBER_OF_PEOPLE);
        }

        Contract contract = contractMapper.toContract(request);
        contract.setRoom(room);
        contract.setRoomPrice(room.getPrice());
        contract.setTenants(tenants);
        contract.setAssets(assets);
        contract.setServices(services);
        contract.setVehicles(vehicles);
        contract.setContractCode(codeGeneratorService.generateContractCode(room));

        applyUtilityPrices(contract);

        contract.setCreatedAt(Instant.now());
        contract.setUpdatedAt(Instant.now());

        contractRepository.save(contract);

        return contractMapper.toContractResponse(contract);
    }

    @Transactional
    @Override
    public ContractResponse updateContract(String contractId, ContractUpdateRequest request) {
        Contract contract = contractRepository
                .findById(contractId)
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

        Set<Asset> assets = new HashSet<>(assetRepository.findAllById(request.getAssets()));
        if (assets.size() != request.getAssets().size()) {
            throw new AppException(ErrorCode.ASSET_NOT_FOUND);
        }

        Set<com.example.datn_qlnt_manager.entity.Service> services = new HashSet<>();
        if (request.getServices() != null && !request.getServices().isEmpty()) {
            services = new HashSet<>(serviceRepository.findAllById(request.getServices()));
            if (services.size() != request.getServices().size()) {
                throw new AppException(ErrorCode.SERVICE_NOT_FOUND);
            }
        }

        Set<Vehicle> vehicles = new HashSet<>();
        if (request.getVehicles() != null && !request.getVehicles().isEmpty()) {
            vehicles = new HashSet<>(vehicleRepository.findAllById(request.getVehicles()));
            if (vehicles.size() != request.getVehicles().size()) {
                throw new AppException(ErrorCode.VEHICLE_NOT_FOUND);
            }
        }

        contractMapper.updateContract(request, contract);

        contract.setTenants(tenants);
        contract.setAssets(assets);
        contract.setServices(services);
        contract.setVehicles(vehicles);
        contract.setUpdatedAt(Instant.now());

        contractRepository.save(contract);

        return contractMapper.toContractResponse(contract);
    }

    @Override
    public ContractDetailResponse getContractDetailById(String contractId) {
        Contract contract = contractRepository
                .findById(contractId)
                .orElseThrow(() -> new AppException(ErrorCode.CONTRACT_NOT_FOUND));

        ContractDetailResponse detail = contractRepository
                .findContractDetailById(contractId)
                .orElseThrow(() -> new AppException(ErrorCode.CONTRACT_NOT_FOUND));

        List<TenantBasicResponse> tenants = tenantRepository.findTenantsByContractId(contractId);
        detail.setTenants(new HashSet<>(tenants));

        Set<AssetBasicResponse> assetResponses = contract.getAssets().stream()
                .map(asset -> AssetBasicResponse.builder()
                        .id(asset.getId())
                        .nameAsset(asset.getNameAsset())
                        .assetType(asset.getAssetType())
                        .description(asset.getDescriptionAsset())
                        .build())
                .collect(Collectors.toSet());
        detail.setAssets(assetResponses);

        Set<ServiceBasicResponse> serviceResponses = contract.getServices().stream()
                .map(service -> ServiceBasicResponse.builder()
                        .id(service.getId())
                        .name(service.getName())
                        .category(service.getServiceCategory())
                        .unit(service.getUnit())
                        .description(service.getDescription())
                        .build())
                .collect(Collectors.toSet());
        detail.setServices(serviceResponses);

        Set<VehicleBasicResponse> vehicleResponses = contract.getVehicles().stream()
                .map(vehicle -> VehicleBasicResponse.builder()
                        .id(vehicle.getId())
                        .vehicleType(vehicle.getVehicleType())
                        .licensePlate(vehicle.getLicensePlate())
                        .description(vehicle.getDescribe())
                        .build())
                .collect(Collectors.toSet());
        detail.setVehicles(vehicleResponses);

        return detail;
    }

    @Override
    public List<ContractResponse> getAllContractsByUserId() {
        User user = userService.getCurrentUser();
        List<Contract> contracts = contractRepository.findAllContractByUserId(user.getId());
        return contracts.stream().map(contractMapper::toContractResponse).toList();
    }

    @Override
    public void toggleContractStatusById(String contractId) {
        Contract contract = contractRepository
                .findById(contractId)
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

    private void validateRoomForContract(Room room, ContractCreationRequest request) {
        if (request.getNumberOfPeople() > room.getMaximumPeople()) {
            throw new AppException(ErrorCode.NUMBER_OF_PEOPLE_EXCEEDS_LIMIT);
        }

        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new AppException(ErrorCode.END_DATE_BEFORE_START_DATE);
        }

        if (request.getTenants().size() > request.getNumberOfPeople()) {
            throw new AppException(ErrorCode.TENANTS_EXCEEDS_NUMBER_OF_PEOPLE);
        }
    }

    private void applyUtilityPrices(Contract contract) {
        for (com.example.datn_qlnt_manager.entity.Service service : contract.getServices()) {
            if (service.getServiceCategory() == ServiceCategory.DIEN) {
                contract.setElectricPrice(service.getPrice());
            } else if (service.getServiceCategory() == ServiceCategory.NUOC) {
                contract.setWaterPrice(service.getPrice());
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


}
