package com.example.datn_qlnt_manager.service.implement;

import com.example.datn_qlnt_manager.common.Meta;
import com.example.datn_qlnt_manager.common.Pagination;
import com.example.datn_qlnt_manager.dto.PaginatedResponse;
import com.example.datn_qlnt_manager.dto.request.asset.AssetCreationRequest;
import com.example.datn_qlnt_manager.dto.request.asset.AssetUpdateRequest;
import com.example.datn_qlnt_manager.dto.response.IdAndName;
import com.example.datn_qlnt_manager.dto.response.asset.CreateAssetInitResponse;
import com.example.datn_qlnt_manager.dto.response.asset.AssetResponse;
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
import java.util.List;

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
        switch (request.getAssetBeLongTo()) {
            case PHONG -> {
                Room room = roomRepository.findById(request.getRoomID())
                        .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_FOUND));
                asset.setRoom(room);
            }
            case CHUNG -> {
                if (request.getFloorID() != null) {
                    Floor floor = floorRepository.findById(request.getFloorID())
                            .orElseThrow(() -> new AppException(ErrorCode.FLOOR_NOT_FOUND));
                    asset.setFloor(floor);
                } else if (request.getBuildingID() != null) {
                    Building building = buildingRepository.findById(request.getBuildingID())
                            .orElseThrow(() -> new AppException(ErrorCode.BUILDING_NOT_FOUND));
                    asset.setBuilding(building);
                }
            }
            case CA_NHAN -> {
                Tenant tenant = tenantRepository.findById(request.getTenantId())
                        .orElseThrow(() -> new AppException(ErrorCode.TENANT_NOT_FOUND));
                asset.setTenant(tenant);
            }
        }
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
    public PaginatedResponse<AssetResponse> getAllAssets(String nameAsset, int page, int size) {
        var user = userService.getCurrentUser();
        Pageable pageable = PageRequest.of(Math.max(0, page - 1), size, Sort.by(Sort.Direction.DESC, "updatedAt"));
        Page<Asset> assets = assetRepository.searchAssets(nameAsset, user.getId(), pageable);

        List<AssetResponse> responses = assets.getContent().stream().map(assetMapper::toResponse).toList();

        Meta<?> meta = Meta.builder()
                .pagination(Pagination.builder()
                        .count(assets.getNumberOfElements())
                        .perPage(size)
                        .currentPage(page)
                        .totalPages(assets.getTotalPages())
                        .total(assets.getTotalElements())
                        .build())
                .build();

        return PaginatedResponse.<AssetResponse>builder()
                .data(responses)
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

        switch (request.getAssetBeLongTo()) {
            case PHONG -> {
                Room room = roomRepository.findById(request.getRoomID())
                        .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_FOUND));
                asset.setRoom(room);
            }
            case CHUNG -> {
                if (request.getFloorID() != null) {
                    asset.setFloor(floorRepository.findById(request.getFloorID())
                            .orElseThrow(() -> new AppException(ErrorCode.FLOOR_NOT_FOUND)));
                } else if (request.getBuildingID() != null) {
                    asset.setBuilding(buildingRepository.findById(request.getBuildingID())
                            .orElseThrow(() -> new AppException(ErrorCode.BUILDING_NOT_FOUND)));
                }
            }
            case CA_NHAN -> asset.setTenant(tenantRepository.findById(request.getTenantId())
                    .orElseThrow(() -> new AppException(ErrorCode.TENANT_NOT_FOUND)));
        }
        asset.setUpdatedAt(Instant.now());
        return assetMapper.toResponse(assetRepository.save(asset));
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

        List<IdAndName> rooms = roomRepository.findRoomsByUserId(user.getId())
                .stream()
                .map(r -> new IdAndName(r.getId(), r.getName()))
                .toList();

        List<IdAndName> buildings = buildingRepository.findAllBuildingsByUserId(user.getId())
                .stream()
                .map(b -> new IdAndName(b.getId(), b.getName()))
                .toList();

        List<IdAndName> floors = floorRepository.findAllFloorsByUserId(user.getId())
                .stream()
                .map(f -> new IdAndName(f.getId(), f.getName()))
                .toList();

        List<IdAndName> tenants = tenantRepository.findAllTenantsByOwnerId(user.getId())
                .stream()
                .map(t -> new IdAndName(t.getId(), t.getName()))
                .toList();

        return CreateAssetInitResponse.builder()
                .assetTypes(assetTypes)
                .buildings(buildings)
                .floors(floors)
                .tenants(tenants)
                .rooms(rooms)
                .build();
    }
}