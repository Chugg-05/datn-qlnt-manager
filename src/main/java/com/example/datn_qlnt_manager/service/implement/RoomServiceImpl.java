package com.example.datn_qlnt_manager.service.implement;

import java.time.Instant;
import java.util.List;

import com.example.datn_qlnt_manager.common.RoomStatus;
import com.example.datn_qlnt_manager.dto.PaginatedResponse;
import com.example.datn_qlnt_manager.dto.filter.RoomFilter;
import com.example.datn_qlnt_manager.dto.request.room.RoomCreationRequest;
import com.example.datn_qlnt_manager.dto.request.room.RoomUpdateRequest;
import com.example.datn_qlnt_manager.dto.response.room.RoomCountResponse;
import com.example.datn_qlnt_manager.entity.Building;
import com.example.datn_qlnt_manager.entity.Floor;
import com.example.datn_qlnt_manager.entity.User;
import com.example.datn_qlnt_manager.exception.AppException;
import com.example.datn_qlnt_manager.exception.ErrorCode;
import com.example.datn_qlnt_manager.repository.FloorRepository;
import com.example.datn_qlnt_manager.service.UserService;
import jakarta.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.datn_qlnt_manager.common.Meta;
import com.example.datn_qlnt_manager.common.Pagination;
import com.example.datn_qlnt_manager.dto.response.room.RoomResponse;
import com.example.datn_qlnt_manager.entity.Room;
import com.example.datn_qlnt_manager.mapper.RoomMapper;
import com.example.datn_qlnt_manager.repository.RoomRepository;
import com.example.datn_qlnt_manager.service.RoomService;

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
    UserService userService;
    CodeGeneratorService codeGeneratorService;

    @Override
    public PaginatedResponse<RoomResponse> getPageAndSearchAndFilterRoomByUserId(
            RoomFilter roomFilter,
            Integer page,
            Integer size
    ) {
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

        return  buildPaginatedRoomResponse(paging, page, size);
    }

    @Override
    public PaginatedResponse<RoomResponse> getRoomWithStatusCancelByUserId(
            RoomFilter roomFilter,
            Integer page,
            Integer size
    ) {
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

        return  buildPaginatedRoomResponse(paging, page, size);
    }

    @Override
    public RoomResponse createRoom(RoomCreationRequest request) {
        Floor floor = floorRepository.findById(request.getFloorId())
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
        Room existingRoom =
                roomRepository.findById(roomId).orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_FOUND));

        Floor floor = floorRepository
                .findById(request.getFloorId())
                .orElseThrow(() -> new AppException(ErrorCode.FLOOR_NOT_FOUND));

        Room room = roomMapper.toRoomUpdate(request);

        room.setId(existingRoom.getId());

        room.setRoomCode(existingRoom.getRoomCode());
        room.setFloor(floor);
        room.setCreatedAt(existingRoom.getCreatedAt());
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

    private PaginatedResponse<RoomResponse> buildPaginatedRoomResponse(
            Page<Room> paging, int page, int size) {

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

        return PaginatedResponse.<RoomResponse>builder()
                .data(rooms)
                .meta(meta)
                .build();
    }
}
