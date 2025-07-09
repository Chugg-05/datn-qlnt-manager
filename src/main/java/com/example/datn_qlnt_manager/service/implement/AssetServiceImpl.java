package com.example.datn_qlnt_manager.service.implement;

import com.example.datn_qlnt_manager.common.AssetBeLongTo;
import com.example.datn_qlnt_manager.common.ContractStatus;
import com.example.datn_qlnt_manager.common.Meta;
import com.example.datn_qlnt_manager.common.Pagination;
import com.example.datn_qlnt_manager.dto.PaginatedResponse;
import com.example.datn_qlnt_manager.dto.filter.AssetFilter;
import com.example.datn_qlnt_manager.dto.request.asset.AssetCreationRequest;
import com.example.datn_qlnt_manager.dto.request.asset.AssetUpdateRequest;
import com.example.datn_qlnt_manager.dto.response.IdAndName;
import com.example.datn_qlnt_manager.dto.response.asset.CreateAssetInitResponse;
import com.example.datn_qlnt_manager.dto.response.asset.AssetResponse;
import com.example.datn_qlnt_manager.dto.response.building.BuildingSelectResponse;
import com.example.datn_qlnt_manager.dto.response.floor.FloorSelectResponse;
import com.example.datn_qlnt_manager.dto.response.room.RoomSelectResponse;
import com.example.datn_qlnt_manager.dto.response.tenant.TenantSelectResponse;
import com.example.datn_qlnt_manager.entity.*;
import com.example.datn_qlnt_manager.exception.AppException;
import com.example.datn_qlnt_manager.exception.ErrorCode;
import com.example.datn_qlnt_manager.mapper.AssetMapper;
import com.example.datn_qlnt_manager.repository.*;
import com.example.datn_qlnt_manager.service.AssetService;
import com.example.datn_qlnt_manager.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AssetServiceImpl implements AssetService {

    AssetMapper assetMapper;
    AssetRepository assetRepository;
    AssetTypeRepository assetTypeRepository;
    RoomRepository roomRepository;
    BuildingRepository buildingRepository;
    FloorRepository floorRepository;
    TenantRepository tenantRepository;
    UserService userService;


    @Override
    public AssetResponse createAsset(AssetCreationRequest request) {
        List<Asset> duplicates = assetRepository.findByNameAssetIgnoreCase(request.getNameAsset());
        if (!duplicates.isEmpty()) {
            throw new AppException(ErrorCode.DUPLICATE_ASSET_NAME);
        }

        Asset asset = assetMapper.toAsset(request);

        // set AssetType
        AssetType assetType = assetTypeRepository.findById(request.getAssetTypeId())
                .orElseThrow(() -> new AppException(ErrorCode.ASSET_TYPE_NOT_FOUND));
        asset.setAssetType(assetType);

        // Xử lý theo loại tài sản thuộc về đâu
        addOrUpdateAsset(asset, request.getAssetBeLongTo(), request.getRoomID(), request.getFloorID(), request.getBuildingID(), request.getTenantId());

        asset.setCreatedAt(Instant.now());
        asset.setUpdatedAt(Instant.now());

        return assetMapper.toResponse(assetRepository.save(asset));
    }

    @Override
    public void deleteAssetById(String assetId) {
        if (!assetRepository.existsById(assetId)) {
            throw new AppException(ErrorCode.ASSET_NOT_FOUND);
        }
        assetRepository.deleteById(assetId);
    }

    @Override
    public PaginatedResponse<AssetResponse> getPageAndSearchAndFilterAssetByUserId(AssetFilter filter, int page,
                                                                                   int size) {
        User currentUser = userService.getCurrentUser();

        Pageable pageable = PageRequest.of(Math.max(0, page - 1), size, Sort.by("updatedAt").descending());

        Page<Asset> pageAsset = assetRepository.findAllByFilterAndUserId(
                filter.getNameAsset(),
                filter.getAssetBeLongTo(),
                filter.getAssetStatus(),
                currentUser.getId(),
                pageable
        );

        List<AssetResponse> assetResponses = pageAsset.getContent()
                .stream()
                .map(assetMapper::toResponse)
                .toList();

        Meta<?> meta = Meta.builder()
                .pagination(Pagination.builder()
                        .count(pageAsset.getNumberOfElements())
                        .perPage(size)
                        .currentPage(page)
                        .totalPages(pageAsset.getTotalPages())
                        .total(pageAsset.getTotalElements())
                        .build())
                .build();

        return PaginatedResponse.<AssetResponse>builder()
                .data(assetResponses)
                .meta(meta)
                .build();
    }

    @Override
    public AssetResponse updateAssetById(String assetId, AssetUpdateRequest request) {
        Asset asset = assetRepository.findById(assetId)
                .orElseThrow(() -> new AppException(ErrorCode.ASSET_NOT_FOUND));
        assetMapper.updateAsset(asset, request);

        List<Asset> duplicates = assetRepository.findByNameAssetIgnoreCaseAndIdNot(request.getNameAsset(), assetId);
        if (!duplicates.isEmpty()) {
            throw new AppException(ErrorCode.DUPLICATE_ASSET_NAME);
        }

        // set AssetType
        AssetType assetType = assetTypeRepository.findById(request.getAssetTypeId())
                .orElseThrow(() -> new AppException(ErrorCode.ASSET_TYPE_NOT_FOUND));
        asset.setAssetType(assetType);

        // clear id cũ - gán id liên quan khi sửa belongto
        asset.setRoom(null);
        asset.setBuilding(null);
        asset.setFloor(null);
        asset.setTenant(null);

        addOrUpdateAsset(asset, request.getAssetBeLongTo(), request.getRoomID(), request.getFloorID(), request.getBuildingID(), request.getTenantId());

        asset.setUpdatedAt(Instant.now());
        return assetMapper.toResponse(assetRepository.save(asset));
    }

    private void addOrUpdateAsset(Asset asset, AssetBeLongTo assetBeLongTo, String roomID, String floorID, String buildingID, String tenantId) {
        switch (assetBeLongTo) {
            case PHONG -> {
                Room room = roomRepository.findById(roomID)
                        .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_FOUND));
                setRoom(asset, room);
            }

            case CHUNG -> {
                if (floorID != null) {
                    Floor floor = floorRepository.findById(floorID)
                            .orElseThrow(() -> new AppException(ErrorCode.FLOOR_NOT_FOUND));
                    asset.setFloor(floor);

                    Building building = floor.getBuilding();
                    if (building != null) {
                        asset.setBuilding(building);
                    }
                } else if (buildingID != null) {
                    Building building = buildingRepository.findById(buildingID)
                            .orElseThrow(() -> new AppException(ErrorCode.BUILDING_NOT_FOUND));
                    asset.setBuilding(building);
                }
            }

            case CA_NHAN -> {
                Tenant tenant = tenantRepository.findById(tenantId)
                        .orElseThrow(() -> new AppException(ErrorCode.TENANT_NOT_FOUND));
                asset.setTenant(tenant);

                Contract latestValidContract = tenant.getContracts().stream()
                        .filter(contract -> contract.getStatus() == ContractStatus.HIEU_LUC
                                || contract.getStatus() == ContractStatus.SAP_HET_HAN)
                        .max(Comparator.comparing(Contract::getStartDate))
                        .orElse(null);

                if (latestValidContract != null) {
                    Room room = latestValidContract.getRoom();
                    if (room != null) {
                        setRoom(asset, room);
                    }
                } else {
                    throw new AppException(ErrorCode.TENANT_HAS_NO_CONTRACT);
                }
            }

        }
    }

    private void setRoom(Asset asset, Room room) {
        asset.setRoom(room);

        Floor floor = room.getFloor();
        if (floor != null) {
            asset.setFloor(floor);

            Building building = floor.getBuilding();
            if (building != null) {
                asset.setBuilding(building);
            }
        }
    }

    @Override
    public List<AssetResponse> findAssetsByCurrentUser() {
        String currentUserId = userService.getCurrentUser().getId(); // đảm bảo lấy từ token
        List<Asset> assets = assetRepository.findAssetsByUserId(currentUserId);
        return assets.stream()
                .map(assetMapper::toResponse)
                .toList();
    }

    @Override
    public CreateAssetInitResponse getInitDataForAssetCreation() {
        User user = userService.getCurrentUser();

        List<IdAndName> assetTypes =
                assetTypeRepository.findAllByUserId(user.getId())
                        .stream().map(at -> new IdAndName(at.getId(),
                                at.getNameAssetType())).toList();

        List<BuildingSelectResponse> buildings = buildingRepository.findAllBuildingsByUserId(user.getId())
                .stream()
                .map(b -> {
                    List<FloorSelectResponse> floorSelectResponses =
                            floorRepository.findAllFloorsByUserIdAndBuildingId(user.getId(), b.getId()).stream()
                                    .map(f -> {
                                        List<RoomSelectResponse> roomSelectResponses =
                                                roomRepository.findRoomsByUserIdAndFloorId(user.getId(), f.getId())
                                                        .stream().map(r -> {
                                                            List<TenantSelectResponse> tenants =
                                                                    tenantRepository.findAllTenantsByOwnerIdAndRoomId(user.getId(), r.getId()).stream().toList();

                                                            return RoomSelectResponse.builder()
                                                                    .id(r.getId())
                                                                    .name(r.getName())
                                                                    .tenants(tenants)
                                                                    .build();
                                                        }).toList();
                                        return FloorSelectResponse.builder()
                                                .id(f.getId())
                                                .name(f.getName())
                                                .rooms(roomSelectResponses)
                                                .build();
                                    }).toList();
                    return BuildingSelectResponse.builder()
                            .id(b.getId())
                            .name(b.getName())
                            .floors(floorSelectResponses)
                            .build();
                })
                .toList();

        return CreateAssetInitResponse.builder()
                .assetTypes(assetTypes)
                .buildings(buildings)
                .build();
    }
}