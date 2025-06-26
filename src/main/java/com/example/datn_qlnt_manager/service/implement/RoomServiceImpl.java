package com.example.datn_qlnt_manager.service.implement;

import java.time.Instant;
import java.util.List;

import com.example.datn_qlnt_manager.common.RoomStatus;
import com.example.datn_qlnt_manager.dto.PaginatedResponse;
import com.example.datn_qlnt_manager.dto.filter.RoomFilter;
import com.example.datn_qlnt_manager.dto.request.room.RoomCreationRequest;
import com.example.datn_qlnt_manager.dto.request.room.RoomUpdateRequest;
import com.example.datn_qlnt_manager.entity.Floor;
import com.example.datn_qlnt_manager.exception.AppException;
import com.example.datn_qlnt_manager.exception.ErrorCode;
import com.example.datn_qlnt_manager.repository.FloorRepository;
import jakarta.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    @Override
    public PaginatedResponse<RoomResponse> filterRooms(Integer page, Integer size, RoomFilter roomFilter) {
        Pageable pageable = PageRequest.of(Math.max(0, page - 1), size, Sort.by(Sort.Order.desc("createdAt")));

        Page<Room> paging = roomRepository.filterRoomsPaging(
                roomFilter.getStatus(),
                roomFilter.getMaxPrice(),
                roomFilter.getMinPrice(),
                roomFilter.getMaxAcreage(),
                roomFilter.getMinAcreage(),
                roomFilter.getMaximumPeople(),
                roomFilter.getNameFloor(),
                pageable);

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
    public RoomResponse createRoom(RoomCreationRequest request) {
        if (roomRepository.existsByRoomId(request.getRoomCode())) {
            throw new AppException(ErrorCode.ROOM_CODE_EXISTED);
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
    public Void deleteRoom(String roomId) {
        roomRepository.findById(roomId).orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_FOUND));

        roomRepository.deleteById(roomId);
        return null;
    }

    @Override
    public RoomResponse updateRoomStatus(String roomId, RoomStatus status) {
        Room room = roomRepository.findById(roomId).orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_FOUND));

        room.setStatus(status);
        room.setUpdatedAt(Instant.now());

        return roomMapper.toRoomResponse(roomRepository.save(room));
    }
}
