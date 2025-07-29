package com.example.datn_qlnt_manager.service.implement;

import java.time.Instant;
import java.util.*;

import com.example.datn_qlnt_manager.common.*;
import com.example.datn_qlnt_manager.common.AssetType;
import com.example.datn_qlnt_manager.dto.filter.AssetFilter;
import com.example.datn_qlnt_manager.dto.request.asset.AssetUpdateRequest;
import com.example.datn_qlnt_manager.dto.statistics.AssetStatusStatistic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.example.datn_qlnt_manager.dto.PaginatedResponse;
import com.example.datn_qlnt_manager.dto.request.asset.AssetCreationRequest;
import com.example.datn_qlnt_manager.dto.response.IdAndName;
import com.example.datn_qlnt_manager.dto.response.asset.CreateAssetInit2Response;
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
        User user = userService.getCurrentUser();
        List<Asset> duplicates = assetRepository.findByNameAssetIgnoreCase(request.getNameAsset());
        if (!duplicates.isEmpty()) {
            throw new AppException(ErrorCode.DUPLICATE_ASSET_NAME);
        }

        if (request.getAssetType() == AssetType.AN_NINH && request.getAssetBeLongTo() != AssetBeLongTo.CHUNG) {
            throw new AppException(ErrorCode.INVALID_SECURITY_ASSET_LOCATION);
        }

        Asset asset = assetMapper.toAsset(request);

        asset.setAssetStatus(AssetStatus.HOAT_DONG);
        asset.setUser(user);
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
                filter.getAssetType(),
                filter.getAssetBeLongTo(),
                filter.getAssetStatus(),
                currentUser.getId(),
                pageable);

        List<AssetResponse> assetResponses = pageAsset.getContent().stream().map(assetMapper::toResponse).toList();

        Meta<?> meta =
                Meta.builder().pagination(Pagination.builder().count(pageAsset.getNumberOfElements()).perPage(size).currentPage(page).totalPages(pageAsset.getTotalPages()).total(pageAsset.getTotalElements()).build()).build();

        return PaginatedResponse.<AssetResponse>builder().data(assetResponses).meta(meta).build();
    }

    @Override
    public AssetResponse updateAssetById(String assetId, AssetUpdateRequest request) {
        Asset asset = assetRepository.findById(assetId).orElseThrow(() -> new AppException(ErrorCode.ASSET_NOT_FOUND));
        assetMapper.updateAsset(asset, request);
        asset.setAssetStatus(request.getAssetStatus());
        List<Asset> duplicates = assetRepository.findByNameAssetIgnoreCaseAndIdNot(request.getNameAsset(), assetId);
        if (!duplicates.isEmpty()) {
            throw new AppException(ErrorCode.DUPLICATE_ASSET_NAME);
        }

        asset.setUpdatedAt(Instant.now());
        return assetMapper.toResponse(assetRepository.save(asset));
    }

    @Override
    public List<AssetResponse> findAssetsByCurrentUser() {
        String currentUserId = userService.getCurrentUser().getId(); // đảm bảo lấy từ token
        List<Asset> assets = assetRepository.findAssetsByUserId(currentUserId);
        return assets.stream().map(assetMapper::toResponse).toList();
    }

    @Override
    public void toggleAsseStatus(String assetId) {
        Asset asset = assetRepository.findById(assetId)
                .orElseThrow(() -> new AppException(ErrorCode.ASSET_NOT_FOUND));

        if (asset.getAssetStatus() == AssetStatus.HOAT_DONG) {
            asset.setAssetStatus(AssetStatus.KHONG_SU_DUNG);
            asset.setUpdatedAt(Instant.now());
        } else if (asset.getAssetStatus() == AssetStatus.KHONG_SU_DUNG) {
            asset.setAssetStatus(AssetStatus.HOAT_DONG);
            asset.setUpdatedAt(Instant.now());
        } else {
            throw new AppException(ErrorCode.CANNOT_TOGGLE_ASSET_STATUS);
        }
        assetRepository.save(asset);
    }

    @Override
    public CreateAssetInitResponse getInitDataForAssetCreation() {
        User user = userService.getCurrentUser();

        List<IdAndName> assetTypes =
                assetTypeRepository.findAllByUserId(user.getId()).stream().map(at -> new IdAndName(at.getId(),
                        at.getNameAssetType())).toList();

        List<BuildingSelectResponse> buildings =
                buildingRepository.findAllBuildingsByUserId(user.getId()).stream().map(b -> {
                    List<FloorSelectResponse> floorSelectResponses =
                            floorRepository.findAllFloorsByUserIdAndBuildingId(user.getId(), b.getId()).stream().map(f -> {
                                List<RoomSelectResponse> roomSelectResponses =
                                        roomRepository.findRoomsByUserIdAndFloorId(user.getId(), f.getId()).stream().map(r -> {
                                            List<TenantSelectResponse> tenants =
                                                    tenantRepository.findAllTenantsByOwnerIdAndRoomId(user.getId(),
                                                            r.getId()).stream().toList();

                                            return RoomSelectResponse.builder().id(r.getId()).name(r.getName()).tenants(tenants).build();
                                        }).toList();
                                return FloorSelectResponse.builder().id(f.getId()).name(f.getName()).rooms(roomSelectResponses).build();
                            }).toList();
                    return BuildingSelectResponse.builder().id(b.getId()).name(b.getName()).floors(floorSelectResponses).build();
                }).toList();

        return CreateAssetInitResponse.builder().assetTypes(assetTypes).buildings(buildings).build();
    }

    @Override
    public CreateAssetInit2Response getAssetsInfoByUserId2() {
        User user = userService.getCurrentUser();

        List<IdAndName> assetTypes =
                assetTypeRepository.findAllByUserId(user.getId()).stream().map(at -> new IdAndName(at.getId(),
                        at.getNameAssetType())).toList();

        List<IdAndName> buildings =
                buildingRepository.findAllBuildingsByUserId(user.getId()).stream().map(b -> new IdAndName(b.getId(),
                        b.getName())).toList();

        List<IdAndName> floors =
                floorRepository.getFloorsByUserId(user.getId()).stream().map(b -> new IdAndName(b.getId(),
                        b.getName())).toList();

        List<IdAndName> rooms =
                roomRepository.findAllRoomsByUserId(user.getId()).stream().map(b -> new IdAndName(b.getId(),
                        b.getRoomCode())).toList();

        List<IdAndName> tenants =
                tenantRepository.findAllTenantsByUserId(user.getId()).stream().map(b -> new IdAndName(b.getId(),
                        b.getFullName())).toList();

        return CreateAssetInit2Response.builder().assetTypes(assetTypes).buildings(buildings).floors(floors).tenants(tenants).rooms(rooms).build();
    }

    @Override
    public AssetStatusStatistic getAssetStatisticsByUserId() {
        User user = userService.getCurrentUser();
        return assetRepository.getAssetStatisticsByUserId(user.getId());
    }

    @Override
    public void softDeleteAsset(String assetId) {
        Asset asset = assetRepository.findById(assetId)
                .orElseThrow(() -> new AppException(ErrorCode.ASSET_NOT_FOUND));
        asset.setAssetStatus(AssetStatus.KHONG_SU_DUNG);
        assetRepository.save(asset);
    }
}
