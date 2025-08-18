package com.example.datn_qlnt_manager.service.implement;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.example.datn_qlnt_manager.common.*;
import com.example.datn_qlnt_manager.dto.PaginatedResponse;
import com.example.datn_qlnt_manager.dto.filter.AssetRoomFilter;
import com.example.datn_qlnt_manager.dto.projection.AssetRoomView;
import com.example.datn_qlnt_manager.dto.request.assetRoom.*;
import com.example.datn_qlnt_manager.dto.response.asset.AssetDetailResponse;
import com.example.datn_qlnt_manager.dto.response.asset.AssetLittleResponse;
import com.example.datn_qlnt_manager.dto.response.assetRoom.AssetRoomDetailResponse;
import com.example.datn_qlnt_manager.dto.response.assetRoom.AssetRoomResponse;
import com.example.datn_qlnt_manager.dto.response.room.RoomBasicResponse;
import com.example.datn_qlnt_manager.dto.statistics.AssetStatusStatistic;
import com.example.datn_qlnt_manager.entity.*;
import com.example.datn_qlnt_manager.exception.AppException;
import com.example.datn_qlnt_manager.exception.ErrorCode;
import com.example.datn_qlnt_manager.mapper.AssetRoomMapper;
import com.example.datn_qlnt_manager.repository.AssetRepository;
import com.example.datn_qlnt_manager.repository.AssetRoomRepository;
import com.example.datn_qlnt_manager.repository.BuildingRepository;
import com.example.datn_qlnt_manager.repository.RoomRepository;
import com.example.datn_qlnt_manager.service.AssetRoomService;
import com.example.datn_qlnt_manager.service.UserService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AssetRoomServiceImpl implements AssetRoomService {
    AssetRoomRepository assetRoomRepository;
    RoomRepository roomRepository;
    AssetRepository assetRepository;
    BuildingRepository buildingRepository;
    UserService userService;
    AssetRoomMapper assetRoomMapper;

    @Override
    public PaginatedResponse<AssetRoomView> getAssetRoomsPaging(AssetRoomFilter filter, int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(0, page - 1), size);
        User user = userService.getCurrentUser();

        Page<AssetRoomView> paging = assetRoomRepository.getAssetRoomsPaging(
                user.getId(),
                filter.getQuery(),
                filter.getBuilding(),
                filter.getFloor(),
                filter.getRoomType(),
                filter.getStatus(),
                pageable);

        Meta<?> meta = Meta.builder()
                .pagination(Pagination.builder()
                        .count(paging.getNumberOfElements())
                        .perPage(size)
                        .currentPage(page)
                        .totalPages(paging.getTotalPages())
                        .total(paging.getTotalElements())
                        .build())
                .build();

        return PaginatedResponse.<AssetRoomView>builder()
                .data(paging.getContent())
                .meta(meta)
                .build();
    }

    @Override
    public AssetRoomDetailResponse getAssetRoomDetail(String roomId) {
        Room room = roomRepository.findById(roomId).orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_FOUND));

        return buildAssetRoomDetailResponse(room);
    }

    @Transactional
    @Override
    public AssetRoomResponse createAssetRoom(AssetRoomCreationRequest request) {
        Room room = roomRepository
                .findById(request.getRoomId())
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_FOUND));

        AssetRoom assetRoom;

        switch (request.getAssetBeLongTo()) {
            case PHONG -> {
                Asset asset = getValidAssetForAssignment(request.getAssetId());

                int usedQuantity = Optional.ofNullable(assetRoomRepository.sumQuantityByAssetId(asset.getId()))
                        .orElse(0);

                int availableQuantity = asset.getQuantity() - usedQuantity;

                if (availableQuantity <= 0) {
                    throw new AppException(ErrorCode.ASSET_QUANTITY_NOT_ENOUGH);
                }
                if (request.getQuantity() != null && request.getQuantity() > availableQuantity) {
                    throw new AppException(ErrorCode.ASSET_QUANTITY_NOT_ENOUGH);
                }

                assetRoom = buildAssetRoom(
                        room,
                        asset,
                        AssetBeLongTo.PHONG,
                        "Đã thêm " + asset.getNameAsset() + " vào phòng " + room.getRoomCode());

                if (!assetRoomRepository.existsByRoomAndAsset(room, asset)) {
                    asset.setRemainingQuantity(Math.max(asset.getRemainingQuantity() - 1, 0));
                    assetRepository.save(asset);
                }
            }

            case CA_NHAN -> {
                validatePersonalAsset(request);
                String description = StringUtils.hasText(request.getDescription())
                        ? request.getDescription()
                        : "Tài sản cá nhân " + request.getAssetName() + " được thêm vào phòng " + room.getRoomCode();

                assetRoom = AssetRoom.builder()
                        .room(room)
                        .assetBeLongTo(AssetBeLongTo.CA_NHAN)
                        .assetName(request.getAssetName())
                        .price(request.getPrice())
                        .quantity(request.getQuantity())
                        .assetStatus(AssetStatus.HOAT_DONG)
                        .dateAdded(LocalDate.now())
                        .description(description)
                        .build();
            }

            case CHUNG -> throw new AppException(ErrorCode.PUBLIC_ASSET);
            default -> throw new AppException(ErrorCode.INVALID_ASSET_BELONG_TO);
        }

        assetRoom.setCreatedAt(Instant.now());
        assetRoom.setUpdatedAt(Instant.now());

        AssetRoom saved = assetRoomRepository.save(assetRoom);
        return assetRoomMapper.toAssetRoomResponse(saved);
    }

    @Transactional
    @Override
    public AssetDetailResponse createAssetRoomForBuilding(AssetRoomCreationForBuildingRequest request) {
        Asset asset = getValidAssetForAssignment(request.getAssetId());

        List<Room> rooms = roomRepository.findByFloorBuildingId(request.getBuildingId());

        if (rooms.isEmpty()) {
            throw new AppException(ErrorCode.ROOM_NOT_FOUND_IN_BUILDING);
        }

        int countAsset = assignAssetToRooms(asset, rooms);
        if (countAsset > 0) {
            asset.setRemainingQuantity(Math.max(asset.getRemainingQuantity() - countAsset, 0));
            assetRepository.save(asset);
        }
        return buildAssetDetailResponse(asset, rooms);
    }

    @Transactional
    @Override
    public AssetDetailResponse createAssetRoomForAsset(AssetRoomCreationForAssetRequest request) {
        Asset asset = getValidAssetForAssignment(request.getAssetId());

        List<Room> rooms = roomRepository.findAllById(request.getRoomIds());

        if (rooms.size() != request.getRoomIds().size()) {
            throw new AppException(ErrorCode.ROOM_NOT_FOUND);
        }

        int usedQuantity = Optional.ofNullable(assetRoomRepository.sumQuantityByAssetId(asset.getId()))
                .orElse(0);

        int remaining = asset.getQuantity() - usedQuantity;

        if (rooms.size() > remaining) {
            throw new AppException(ErrorCode.ASSET_QUANTITY_NOT_ENOUGH);
        }

        int countAsset = assignAssetToRooms(asset, rooms);
        if (countAsset > 0) {
            asset.setRemainingQuantity(Math.max(asset.getRemainingQuantity() - countAsset, 0));
            assetRepository.save(asset);
        }
        return buildAssetDetailResponse(asset, rooms);
    }

    @Transactional
    @Override
    public AssetRoomDetailResponse createAssetRoomForRoom(AssetRoomCreationForRoomRequest request) {
        Room room = roomRepository
                .findById(request.getRoomId())
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_FOUND));

        List<Asset> assets = assetRepository.findAllById(request.getAssetIds());

        if (assets.size() != request.getAssetIds().size()) {
            throw new AppException(ErrorCode.ASSET_NOT_FOUND);
        }

        for (Asset asset : assets) {
            getValidAssetForAssignment(asset); // validate từng asset
            int countAsset = assignAssetToRooms(asset, List.of(room));
            if (countAsset > 0) {
                asset.setRemainingQuantity(Math.max(asset.getRemainingQuantity() - countAsset, 0));
            }
        }

        assetRepository.saveAll(assets);
        return buildAssetRoomDetailResponse(room);
    }

    @Override
    public AssetRoomResponse updateAssetRoom(String assetRoomId, AssetRoomUpdateRequest request) {
        AssetRoom assetRoom = assetRoomRepository
                .findById(assetRoomId)
                .orElseThrow(() -> new AppException(ErrorCode.ASSET_ROOM_NOT_FOUND));

        assetRoomMapper.updateAssetRoomFromRequest(request, assetRoom);
        assetRoom.setUpdatedAt(Instant.now());
        assetRoomRepository.save(assetRoom);

        return assetRoomMapper.toAssetRoomResponse(assetRoom);
    }

    @Override
    public AssetStatusStatistic getAssetStatisticsByBuildingId(String buildingId) {
        Building building = buildingRepository
                .findById(buildingId)
                .orElseThrow(() -> new AppException(ErrorCode.BUILDING_NOT_FOUND));
        return assetRoomRepository.getAssetStatisticsByBuildingId(building.getId());
    }

    @Override
    public void toggleAssetRoomStatus(String assetRoomId) {
        AssetRoom assetRoom = assetRoomRepository
                .findById(assetRoomId)
                .orElseThrow(() -> new AppException(ErrorCode.ASSET_ROOM_NOT_FOUND));

        AssetStatus assetRoomStatus = assetRoom.getAssetStatus();

        if (assetRoomStatus == AssetStatus.HOAT_DONG) {
            assetRoom.setAssetStatus(AssetStatus.KHONG_SU_DUNG);
            assetRoom.setUpdatedAt(Instant.now());
        } else if (assetRoomStatus == AssetStatus.KHONG_SU_DUNG) {
            assetRoom.setAssetStatus(AssetStatus.HOAT_DONG);
            assetRoom.setUpdatedAt(Instant.now());
        } else {
            throw new AppException(ErrorCode.CANNOT_TOGGLE_ASSET_STATUS);
        }
        assetRoomRepository.save(assetRoom);
    }

    @Override
    public void deleteAssetRoom(String assetRoomId) {
        AssetRoom assetRoom = assetRoomRepository.findById(assetRoomId).
                orElseThrow(() -> new AppException(ErrorCode.ASSET_ROOM_NOT_FOUND));

        assetRoomRepository.delete(assetRoom);

        if (assetRoom.getAssetBeLongTo() == AssetBeLongTo.PHONG) {
            Asset asset = assetRoom.getAsset();
            asset.setRemainingQuantity(asset.getRemainingQuantity() + 1);
            assetRepository.save(asset);
        }
    }

    private int assignAssetToRooms(Asset asset, List<Room> rooms) {

        int usedQuantity = Optional.ofNullable(assetRoomRepository.sumQuantityByAssetId(asset.getId()))
                .orElse(0);

        int availableQuantity = asset.getQuantity() - usedQuantity;

        if (availableQuantity <= 0) {
            throw new AppException(ErrorCode.ASSET_QUANTITY_NOT_ENOUGH);
        }

        int countAsset = 0;

        for (Room room : rooms) {
            if (assetRoomRepository.existsByRoomAndAsset(room, asset)) continue;

            if (availableQuantity <= 0) break;

            AssetRoom assetRoom = buildAssetRoom(
                    room,
                    asset,
                    asset.getAssetBeLongTo(),
                    "Tài sản " + asset.getNameAsset() + " đã được thêm vào phòng " + room.getRoomCode());

            assetRoom.setCreatedAt(Instant.now());
            assetRoom.setUpdatedAt(Instant.now());

            assetRoomRepository.save(assetRoom);

            countAsset ++;
            availableQuantity--;
        }

        return countAsset;
    }

    private AssetRoom buildAssetRoom(Room room, Asset asset, AssetBeLongTo belongTo, String description) {
        return AssetRoom.builder()
                .room(room)
                .asset(asset)
                .assetBeLongTo(belongTo)
                .assetName(asset.getNameAsset())
                .quantity(1)
                .price(asset.getPrice())
                .dateAdded(LocalDate.now())
                .takeAwayDay(null)
                .assetStatus(asset.getAssetStatus())
                .description(description)
                .build();
    }

    private void validatePersonalAsset(AssetRoomCreationRequest request) {
        if (!StringUtils.hasText(request.getAssetName())) {
            throw new AppException(ErrorCode.ASSET_NAME_NOT_BLANK);
        }

        if (request.getQuantity() == null || request.getQuantity() <= 0) {
            throw new AppException(ErrorCode.QUANTITY_MUST_BE_POSITIVE);
        }

        if (request.getPrice() == null) {
            throw new AppException(ErrorCode.UNIT_PRICE_REQUIRED);
        }
    }

    private Asset getValidAssetForAssignment(String assetId) {
        Asset asset = assetRepository.findById(assetId).orElseThrow(() -> new AppException(ErrorCode.ASSET_NOT_FOUND));
        return getValidAssetForAssignment(asset);
    }

    private Asset getValidAssetForAssignment(Asset asset) {
        if (asset.getAssetStatus() != AssetStatus.HOAT_DONG) {
            throw new AppException(ErrorCode.ASSET_NOT_ACTIVE);
        }
        if (asset.getAssetBeLongTo() == AssetBeLongTo.CHUNG) {
            throw new AppException(ErrorCode.PUBLIC_ASSET);
        }
        return asset;
    }

    private AssetDetailResponse buildAssetDetailResponse(Asset asset, List<Room> rooms) {
        List<RoomBasicResponse> roomResponses = rooms.stream()
                .map(room -> RoomBasicResponse.builder()
                        .id(room.getId())
                        .roomCode(room.getRoomCode())
                        .roomType(room.getRoomType())
                        .status(room.getStatus())
                        .description(room.getDescription())
                        .build())
                .toList();

        return AssetDetailResponse.builder()
                .id(asset.getId())
                .nameAsset(asset.getNameAsset())
                .assetType(asset.getAssetType())
                .assetStatus(asset.getAssetStatus())
                .price(asset.getPrice())
                .description(asset.getDescriptionAsset())
                .rooms(roomResponses)
                .build();
    }

    private AssetRoomDetailResponse buildAssetRoomDetailResponse(Room room) {
        List<AssetRoom> assetRooms = assetRoomRepository.findAllByRoomWithAsset(room);

        List<AssetLittleResponse> assetResponses = assetRooms.stream()
                .map(ar -> AssetLittleResponse.builder()
                        .id(ar.getId())
                        .assetName(ar.getAssetName())
                        .assetBeLongTo(ar.getAssetBeLongTo())
                        .quantity(ar.getQuantity())
                        .price(ar.getPrice())
                        .assetStatus(ar.getAssetStatus())
                        .description(ar.getDescription())
                        .build())
                .toList();

        return AssetRoomDetailResponse.builder()
                .id(room.getId())
                .roomCode(room.getRoomCode())
                .roomType(room.getRoomType())
                .status(room.getStatus())
                .description(room.getDescription())
                .assets(assetResponses)
                .build();
    }
}
