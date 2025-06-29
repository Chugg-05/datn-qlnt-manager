package com.example.datn_qlnt_manager.service.implement;

import com.example.datn_qlnt_manager.common.Meta;
import com.example.datn_qlnt_manager.common.Pagination;
import com.example.datn_qlnt_manager.dto.PaginatedResponse;
import com.example.datn_qlnt_manager.dto.filter.MeterFilter;
import com.example.datn_qlnt_manager.dto.request.meter.MeterCreationRequest;
import com.example.datn_qlnt_manager.dto.request.meter.MeterUpdateRequest;
import com.example.datn_qlnt_manager.dto.response.meter.MeterResponse;
import com.example.datn_qlnt_manager.entity.Meter;
import com.example.datn_qlnt_manager.entity.Room;
import com.example.datn_qlnt_manager.exception.AppException;
import com.example.datn_qlnt_manager.exception.ErrorCode;
import com.example.datn_qlnt_manager.mapper.MeterMapper;
import com.example.datn_qlnt_manager.repository.MeterRepository;
import com.example.datn_qlnt_manager.repository.RoomRepository;
import com.example.datn_qlnt_manager.service.MeterService;
import jakarta.transaction.Transactional;
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
@Transactional
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MeterServiceImpl implements MeterService {

    MeterRepository meterRepository;
    MeterMapper meterMapper;
    RoomRepository roomRepository;


    @Override
    public PaginatedResponse<MeterResponse> filterMeter(Integer page, Integer size, MeterFilter meterFilter) {
        Pageable pageable = PageRequest.of(
                Math.max(0, page - 1),
                size,
                Sort.by(Sort.Order.desc("createdAt")));

        Page<Meter> paging = meterRepository.filterMetersPaging(
                meterFilter.getRoomCode(),
                meterFilter.getMeterType(),
                pageable
        );

        List<MeterResponse> meter = paging.getContent().stream()
                .map(meterMapper::toMeterResponse)
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

        return PaginatedResponse.<MeterResponse>builder().data(meter).meta(meta).build();
    }

    @Override
    public MeterResponse createMeter(MeterCreationRequest request) {
        if (meterRepository.existsById(request.getMeterCode())) {
            throw new AppException(ErrorCode.ROOM_CODE_EXISTED);
        }
        Meter meter = meterMapper.toMeterCreation(request);
        Room room = roomRepository
                .findById(request.getRoomCode())
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_FOUND));
        meter.setRoomCode(room.getId());

        Instant now = Instant.now();
        meter.setCreatedAt(now);
        meter.setUpdatedAt(now);

        return meterMapper.toMeterResponse(meterRepository.save(meter));
    }

    @Override
    public MeterResponse updateMeter(String meterId, MeterUpdateRequest request) {
        Meter existingMeter = meterRepository.findById(meterId)
                .orElseThrow(() -> new AppException(ErrorCode.METER_NOT_FOUND));

        Room room = roomRepository.findById(request.getRoomCode())
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_FOUND));

        Meter electricityWaterMeter = meterMapper.toMeterUpdate(request);

        electricityWaterMeter.setId(existingMeter.getId());

        electricityWaterMeter.setRoomCode(room.getId());
        existingMeter.setCreatedAt(existingMeter.getCreatedAt());
        electricityWaterMeter.setUpdatedAt(Instant.now());

        return meterMapper.toMeterResponse(meterRepository.save(electricityWaterMeter));
    }

    @Override
    public Void deleteMeter(String meterId) {
        meterRepository.findById(meterId)
                .orElseThrow(() -> new AppException(ErrorCode.METER_NOT_FOUND));

        meterRepository.deleteById(meterId);
        return null;
    }
}
