package com.example.datn_qlnt_manager.service.implement;

import java.util.List;

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
public class RoomServiceImpl implements RoomService {

    RoomRepository roomRepository;
    RoomMapper roomMapper;

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
}
