package com.example.datn_qlnt_manager.service.implement;

import java.time.Instant;
import java.util.List;

import com.example.datn_qlnt_manager.dto.response.room.RoomDetailsResponse;
import com.example.datn_qlnt_manager.dto.statistics.RoomNoServiceStatisticResponse;
import com.example.datn_qlnt_manager.dto.statistics.RoomStatisticWithoutAssets;
import com.example.datn_qlnt_manager.dto.statistics.StatisticRoomsWithoutContract;
import com.example.datn_qlnt_manager.entity.*;
import com.example.datn_qlnt_manager.repository.BuildingRepository;
import com.example.datn_qlnt_manager.repository.TenantRepository;
import jakarta.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.datn_qlnt_manager.common.Meta;
import com.example.datn_qlnt_manager.common.Pagination;
import com.example.datn_qlnt_manager.common.RoomStatus;
import com.example.datn_qlnt_manager.dto.PaginatedResponse;
import com.example.datn_qlnt_manager.dto.filter.RoomFilter;
import com.example.datn_qlnt_manager.dto.request.room.RoomCreationRequest;
import com.example.datn_qlnt_manager.dto.request.room.RoomUpdateRequest;
import com.example.datn_qlnt_manager.dto.response.room.RoomCountResponse;
import com.example.datn_qlnt_manager.dto.response.room.RoomResponse;
import com.example.datn_qlnt_manager.exception.AppException;
import com.example.datn_qlnt_manager.exception.ErrorCode;
import com.example.datn_qlnt_manager.mapper.RoomMapper;
import com.example.datn_qlnt_manager.repository.FloorRepository;
import com.example.datn_qlnt_manager.repository.RoomRepository;
import com.example.datn_qlnt_manager.service.RoomService;
import com.example.datn_qlnt_manager.service.UserService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@Transactional
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoomServiceImpl implements RoomService {

    RoomRepository roomRepository;
    RoomMapper roomMapper;
    FloorRepository floorRepository;
    BuildingRepository buildingRepository;
    UserService userService;
    CodeGeneratorService codeGeneratorService;
    private final TenantRepository tenantRepository;

    @Override
    public PaginatedResponse<RoomResponse> getPageAndSearchAndFilterRoomByUserId(
            RoomFilter roomFilter, Integer page, Integer size) {
        User user = userService.getCurrentUser();
        Pageable pageable = PageRequest.of(Math.max(0, page - 1), size);

        Page<Room> paging = roomRepository.getPageAndSearchAndFilterRoomByUserId(
                user.getId(),
                roomFilter.getBuildingId(),
                roomFilter.getStatus(),
                roomFilter.getMaxPrice(),
                roomFilter.getMinPrice(),
                roomFilter.getMaxAcreage(),
                roomFilter.getMinAcreage(),
                roomFilter.getMaximumPeople(),
                roomFilter.getNameFloor(),
                roomFilter.getFloorId(),
                pageable);

        return buildPaginatedRoomResponse(paging, page, size);
    }

    @Override
    public PaginatedResponse<RoomResponse> getRoomWithStatusCancelByUserId(
            RoomFilter roomFilter, Integer page, Integer size) {
        User user = userService.getCurrentUser();
        Pageable pageable = PageRequest.of(Math.max(0, page - 1), size);

        Page<Room> paging = roomRepository.getRoomWithStatusCancelByUserId(
                user.getId(),
                roomFilter.getBuildingId(),
                roomFilter.getMaxPrice(),
                roomFilter.getMinPrice(),
                roomFilter.getMaxAcreage(),
                roomFilter.getMinAcreage(),
                roomFilter.getMaximumPeople(),
                roomFilter.getNameFloor(),
                pageable);

        return buildPaginatedRoomResponse(paging, page, size);
    }

    @Override
    public List<RoomResponse> getRoomsByTenantId() {
        User user = userService.getCurrentUser();
        Tenant tenant = tenantRepository.findByUserId(user.getId())
                .orElseThrow(() -> new AppException(ErrorCode.TENANT_NOT_FOUND));
        List<Room> rooms = roomRepository.findRoomsByTenantId(tenant.getId());
        return rooms.stream()
                .map(roomMapper::toRoomResponse)
                .toList();
    }

    @Override
    public RoomResponse createRoom(RoomCreationRequest request) {
        Floor floor = floorRepository
                .findById(request.getFloorId())
                .orElseThrow(() -> new AppException(ErrorCode.FLOOR_NOT_FOUND));

        Building building = floor.getBuilding();

        int currentRoomCount = roomRepository.countByFloorId(floor.getId());
        if (floor.getMaximumRoom() != null && currentRoomCount >= floor.getMaximumRoom()) {
            throw new AppException(ErrorCode.FLOOR_ROOM_LIMIT_REACHED);
        }

        String roomCode = codeGeneratorService.generateRoomCode(building, floor);

        Room room = roomMapper.toRoomCreation(request);
        room.setFloor(floor);
        room.setRoomCode(roomCode);
        room.setCreatedAt(Instant.now());
        room.setUpdatedAt(Instant.now());

        return roomMapper.toRoomResponse(roomRepository.save(room));
    }

    @Override
    public RoomResponse updateRoom(String roomId, RoomUpdateRequest request) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_FOUND));

        if (room.getStatus() == RoomStatus.DANG_THUE) {
            throw new AppException(ErrorCode.CANNOT_UPDATE_ROOM);
        }

        roomMapper.toRoomUpdate(request, room);

        room.setUpdatedAt(Instant.now());

        return roomMapper.toRoomResponse(roomRepository.save(room));
    }

    @Override
    public List<RoomResponse> getAllRoomsByUserId() {
        User user = userService.getCurrentUser();
        List<Room> rooms = roomRepository.findAllRoomsByUserId(user.getId());
        return rooms.stream().map(roomMapper::toRoomResponse).toList();
    }

    @Override
    public Void deleteRoom(String roomId) {
        roomRepository.findById(roomId).orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_FOUND));

        roomRepository.deleteById(roomId);
        return null;
    }

    @Override
    public void softDeleteRoomById(String id) {
        Room room = roomRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_FOUND));
        room.setPreviousStatus(room.getStatus());
        room.setStatus((RoomStatus.HUY_HOAT_DONG));
        room.setUpdatedAt(Instant.now());

        roomRepository.save(room);
    }

    @Override
    public RoomResponse updateRoomStatus(String roomId, RoomStatus status) {
        Room room = roomRepository.findById(roomId).orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_FOUND));

        room.setStatus(status);
        room.setUpdatedAt(Instant.now());

        return roomMapper.toRoomResponse(roomRepository.save(room));
    }

    @Override
    public RoomCountResponse statisticsRoomByStatus(String buildingId) {
        return roomRepository.getRoomStatsByBuilding(buildingId);
    }

    @Override
    public PaginatedResponse<RoomResponse> getRoomsWithoutServiceByUserId(RoomFilter filter, Integer page, Integer size) {
        User user = userService.getCurrentUser();
        Pageable pageable = PageRequest.of(Math.max(0, page - 1), size);
        Page<Room> paging = roomRepository.findActiveRoomsWithoutServiceRoomByUser(user.getId(), filter.getBuildingId(), pageable);
        return buildPaginatedRoomResponse(paging, page, size);
    }

    @Override
    public StatisticRoomsWithoutContract statisticRoomsWithoutContractByUserId() {
        User user = userService.getCurrentUser();
        long statisticRoomsWithoutContract = roomRepository.statisticRoomsWithoutContract(user.getId());
        return new StatisticRoomsWithoutContract(statisticRoomsWithoutContract);
    }

    @Override
    public RoomStatisticWithoutAssets statisticRoomsWithoutAssetByUserId(String buildingId) {
        User user = userService.getCurrentUser();
        long statisticRoomsWithoutAsset = roomRepository.statisticRoomWithoutAssets(user.getId(), buildingId);
        return new RoomStatisticWithoutAssets(statisticRoomsWithoutAsset);
    }

    @Override
    public PaginatedResponse<RoomResponse> getRoomsWithoutAssets(String buildingId, Integer page, Integer size){
        User user = userService.getCurrentUser();
        if (!buildingRepository.existsById(buildingId)) {
            throw new AppException(ErrorCode.BUILDING_NOT_FOUND);
        }
        Pageable pageable = PageRequest.of(Math.max(0, page - 1), size);
        Page<Room> paging = roomRepository.findRoomsWithoutAssetsByUserId(user.getId(), buildingId, pageable);
        return buildPaginatedRoomResponse(paging, page, size);
    }

    private PaginatedResponse<RoomResponse> buildPaginatedRoomResponse(Page<Room> paging, int page, int size) {

        List<RoomResponse> rooms =
                paging.getContent().stream().map(roomMapper::toRoomResponse).toList();

        Meta<?> meta = Meta.builder()
                .pagination(Pagination.builder()
                        .count(paging.getNumberOfElements())
                        .perPage(size)
                        .currentPage(page)
                        .totalPages(paging.getTotalPages())
                        .total(paging.getTotalElements())
                        .build())
                .build();

        return PaginatedResponse.<RoomResponse>builder().data(rooms).meta(meta).build();
    }

    @Override
    public List<RoomNoServiceStatisticResponse> getRoomNoServiceStatistic(String buildingId) {
        User currentUser = userService.getCurrentUser();
        return roomRepository.countRoomsWithoutServiceByUser(currentUser.getId(), buildingId);
    }

    @Override
    public RoomDetailsResponse getRoomDetails(String roomId) {
        String userId = userService.getCurrentUser().getId();
        return roomRepository.findRoomDetailsForTenant(roomId, userId)
               .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_FOUND));
    }

    @Override
    public RoomResponse restoreRoomById(String roomId) {
        Room room = roomRepository.findById(roomId).orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_FOUND));

        RoomStatus currentStatus = room.getStatus();
        RoomStatus previousStatus = room.getPreviousStatus();

        if (previousStatus != null) {
            room.setPreviousStatus(currentStatus);
            room.setStatus(previousStatus);
        }
        else {
            room.setStatus(RoomStatus.TRONG);
        }

        room.setUpdatedAt(Instant.now());
        return roomMapper.toRoomResponse(roomRepository.save(room));
    }
    @Override
    public List<RoomResponse> findRoomsByBuildingId(String buildingId) {
        List<Room> rooms = roomRepository.findByBuildingId(buildingId);
        return rooms.stream()
                .map(roomMapper::toRoomResponse)
                .toList();
    }
}
