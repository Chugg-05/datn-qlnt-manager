package com.example.datn_qlnt_manager.service.implement;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.example.datn_qlnt_manager.common.RoomStatus;
import com.example.datn_qlnt_manager.dto.request.room.RoomCreationRequest;
import com.example.datn_qlnt_manager.dto.request.room.RoomDeleteRequest;
import com.example.datn_qlnt_manager.dto.request.room.RoomUpdateRequest;
import com.example.datn_qlnt_manager.entity.Floor;
import com.example.datn_qlnt_manager.exception.AppException;
import com.example.datn_qlnt_manager.exception.ErrorCode;
import com.example.datn_qlnt_manager.repository.FloorRepository;
import jakarta.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.datn_qlnt_manager.common.Meta;
import com.example.datn_qlnt_manager.common.Pagination;
import com.example.datn_qlnt_manager.dto.ApiResponse;
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
public class RoomServiceImplementation implements RoomService {

    RoomRepository roomRepository;
    RoomMapper roomMapper;
    FloorRepository floorRepository;

    @Override
    public ApiResponse<List<RoomResponse>> findAll(
            Integer page,
            Integer size,
            String status,
            Double maxPrice,
            Double minPrice,
            Double maxAcreage,
            Double minAcreage,
            Integer maxPerson,
            String nameFloor) {
        int p = page - 1;
        if (p < 0) p = 0;

        Pageable pageable = PageRequest.of(p, size);
        Page<Room> paging = roomRepository.findAllPagingAndFilter(
                status, maxPrice, minPrice, maxAcreage, minAcreage, maxPerson, nameFloor, pageable);

        List<RoomResponse> roomResponses =
                paging.getContent().stream().map(roomMapper::toRoomResponse).toList();

        Meta<?> meta = Meta.builder()
                .pagination(Pagination.builder()
                        .count(paging.getNumberOfElements())
                        .perPage(size)
                        .currentPage(p)
                        .totalPages(paging.getTotalPages())
                        .total(paging.getTotalElements())
                        .build())
                .build();

        return ApiResponse.<List<RoomResponse>>builder()
                .meta(meta)
                .message("Found all rooms successfully")
                .data(roomResponses)
                .build();
    }

    @Override
    public RoomResponse createRoom(RoomCreationRequest request) {
        if(roomRepository.existsByMaPhong(request.getRoomId())){
            throw new AppException(ErrorCode.MA_PHONG_EXISTED);
        }
        Room room = roomMapper.toRoomCreation(request);
        Floor floor = floorRepository
                .findById(request.getFloorId())
                .orElseThrow(() -> new AppException(ErrorCode.FLOOR_NOT_FOUND));
        room.setFloor(floor);

        Instant now = Instant.now();
        room.setCreatedAt(now);
        room.setUpdatedAt(now);

        return roomMapper.toRoomResponse(roomRepository.save(room));
    }

    @Override
    public RoomResponse updateRoom(UUID roomId, RoomUpdateRequest request) {
        Room existingRoom = roomRepository.findById(roomId)
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_FOUND));

        if (!existingRoom.getRoomId().equals(request.getRoomId()) &&
                roomRepository.existsByMaPhong(request.getRoomId())) {
            throw new AppException(ErrorCode.MA_PHONG_EXISTED);
        }

        Floor floor = floorRepository.findById(request.getFloorId())
                .orElseThrow(() -> new AppException(ErrorCode.FLOOR_NOT_FOUND));

        Room room = roomMapper.toRoomUpdate(request);
        room.setId(existingRoom.getId());
        room.setCreatedAt(existingRoom.getCreatedAt());
        room.setFloor(floor);
        room.setUpdatedAt(Instant.now());

        return roomMapper.toRoomResponse(roomRepository.save(room));
    }


    @Override
    public RoomResponse deleteRoom(UUID roomId, RoomDeleteRequest request) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_FOUND));

        Room roomToDelete = roomMapper.toRoomDelete(request);
        roomRepository.delete(roomToDelete);

        return roomMapper.toRoomResponse(roomToDelete);
    }

    @Override
    public RoomResponse updateRoomStatus(UUID roomId, RoomStatus status) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_FOUND));

        room.setStatus(status);
        room.setUpdatedAt(Instant.now());

        return roomMapper.toRoomResponse(roomRepository.save(room));
    }



}
