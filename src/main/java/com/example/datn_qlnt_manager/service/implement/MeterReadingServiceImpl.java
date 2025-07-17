package com.example.datn_qlnt_manager.service.implement;

import com.example.datn_qlnt_manager.common.Meta;
import com.example.datn_qlnt_manager.common.Pagination;
import com.example.datn_qlnt_manager.dto.PaginatedResponse;
import com.example.datn_qlnt_manager.dto.filter.MeterReadingFilter;
import com.example.datn_qlnt_manager.dto.request.meterReading.MeterReadingCreationRequest;
import com.example.datn_qlnt_manager.dto.request.meterReading.MeterReadingUpdateRequest;
import com.example.datn_qlnt_manager.dto.response.meterReading.MeterReadingResponse;
import com.example.datn_qlnt_manager.entity.Meter;
import com.example.datn_qlnt_manager.entity.MeterReading;
import com.example.datn_qlnt_manager.exception.AppException;
import com.example.datn_qlnt_manager.exception.ErrorCode;
import com.example.datn_qlnt_manager.mapper.MeterReadingMapper;
import com.example.datn_qlnt_manager.repository.MeterReadingRepository;
import com.example.datn_qlnt_manager.repository.MeterRepository;
import com.example.datn_qlnt_manager.service.MeterReadingService;
import com.example.datn_qlnt_manager.service.UserService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MeterReadingServiceImpl implements MeterReadingService {

    MeterReadingRepository meterReadingRepository;
    MeterRepository meterRepository;
    MeterReadingMapper meterReadingMapper;
    UserService userService;

    @Override
    public PaginatedResponse<MeterReadingResponse> getPageAndSearchAndFilterMeterReadingByUserId(
            MeterReadingFilter meterReadingFilter, int page, int size) {

        String currentUserId = userService.getCurrentUser().getId();

        Pageable pageable = PageRequest.of(Math.max(0, page - 1), size);

        Page<MeterReading> pageResult = meterReadingRepository.filterMeterReadings(
                currentUserId,
                meterReadingFilter.getBuildingId(),
                meterReadingFilter.getRoomId(),
                meterReadingFilter.getMeterType(),
                meterReadingFilter.getMonth(),
                pageable
        );

        return buildPaginatedMeterReadingResponse(pageResult, page, size);
    }


    @Override
    public MeterReadingResponse createMeterReading(MeterReadingCreationRequest request) {
        Meter meter = meterRepository.findById(request.getMeterId())
                .orElseThrow(() -> new AppException(ErrorCode.METER_NOT_FOUND));

        if (request.getNewIndex() < request.getOldIndex()) {
            throw new AppException(ErrorCode.NEW_INDEX_LESS_THAN_OLD);
        }

        MeterReading meterReading = meterReadingMapper.toMeterReadingCreation(request);
        meterReading.setMeter(meter);
        meterReading.setQuantity(request.getNewIndex() - request.getOldIndex());
        meterReading.setReadingDate(request.getReadingDate());
        meterReading.setCreatedAt(Instant.now());
        meterReading.setUpdatedAt(Instant.now());

        meterReadingRepository.save(meterReading);
        return meterReadingMapper.toResponse(meterReading);
    }

    @Override
    public MeterReadingResponse updateMeterReading(String meterReadingId, MeterReadingUpdateRequest request) {
        MeterReading meterReading = meterReadingRepository.findById(meterReadingId)
                .orElseThrow(() -> new AppException(ErrorCode.METER_READING_NOT_FOUND));

        if (request.getNewIndex() < request.getOldIndex()) {
            throw new AppException(ErrorCode.NEW_INDEX_LESS_THAN_OLD);
        }
        meterReadingMapper.toMeterReadingUpdate(meterReading, request);
        meterReading.setQuantity(request.getNewIndex() - request.getOldIndex());
        meterReading.setUpdatedAt(Instant.now());

        return meterReadingMapper.toResponse(meterReadingRepository.save(meterReading));
    }

    @Override
    public void deleteMeterReading(String meterReadingId) {
        if (!meterReadingRepository.existsById(meterReadingId)) {
            throw new AppException(ErrorCode.METER_READING_NOT_FOUND);
        }
        meterReadingRepository.deleteById(meterReadingId);
    }

    @Override
    public MeterReadingResponse getMeterReadingById(String meterReadingId) {
        return meterReadingRepository.findById(meterReadingId)
                .map(meterReadingMapper::toResponse)
                .orElseThrow(() -> new AppException(ErrorCode.METER_READING_NOT_FOUND));
    }

    private PaginatedResponse<MeterReadingResponse> buildPaginatedMeterReadingResponse(
            Page<MeterReading> pageResult, int page, int size) {

        List<MeterReadingResponse> data = pageResult.getContent().stream()
                .map(meterReadingMapper::toResponse)
                .toList();

        Meta<?> meta = Meta.builder()
                .pagination(Pagination.builder()
                        .count(pageResult.getNumberOfElements())
                        .perPage(size)
                        .currentPage(page)
                        .totalPages(pageResult.getTotalPages())
                        .total(pageResult.getTotalElements())
                        .build())
                .build();

        return PaginatedResponse.<MeterReadingResponse>builder()
                .data(data)
                .meta(meta)
                .build();
    }
}
