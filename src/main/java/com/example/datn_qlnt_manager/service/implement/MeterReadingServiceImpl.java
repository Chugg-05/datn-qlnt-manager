package com.example.datn_qlnt_manager.service.implement;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import jakarta.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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
import com.example.datn_qlnt_manager.repository.InvoiceDetailsRepository;
import com.example.datn_qlnt_manager.repository.MeterReadingRepository;
import com.example.datn_qlnt_manager.repository.MeterRepository;
import com.example.datn_qlnt_manager.service.MeterReadingService;
import com.example.datn_qlnt_manager.service.UserService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@Transactional
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MeterReadingServiceImpl implements MeterReadingService {

    MeterReadingRepository meterReadingRepository;
    MeterRepository meterRepository;
    MeterReadingMapper meterReadingMapper;
    UserService userService;
    InvoiceDetailsRepository invoiceDetailsRepository;

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
                pageable);

        return buildPaginatedMeterReadingResponse(pageResult, page, size);
    }

    @Override
    public MeterReadingResponse createMeterReading(MeterReadingCreationRequest request) {
        Meter meter = meterRepository
                .findById(request.getMeterId())
                .orElseThrow(() -> new AppException(ErrorCode.METER_NOT_FOUND));

        if (request.getMonth() == null && request.getYear() == null) {
            LocalDate now = LocalDate.now();
            request.setMonth(now.getMonthValue());
            request.setYear(now.getYear());
        } else if (request.getMonth() == null) {
            throw new AppException(ErrorCode.MONTH_NOT_FOUND);
        } else if (request.getYear() == null) {
            throw new AppException(ErrorCode.YEAR_NOT_FOUND);
        }

        if (meterReadingRepository.existsByMeterIdAndMonthAndYear(
                request.getMeterId(), request.getMonth(), request.getYear())) {
            throw new AppException(ErrorCode.METER_READING_EXISTED);
        }

        if (request.getNewIndex() < meter.getClosestIndex()) {
            throw new AppException(ErrorCode.NEW_INDEX_LESS_THAN_OLD);
        }

        MeterReading meterReading = meterReadingMapper.toMeterReadingCreation(request);
        meterReading.setMeter(meter);
        meterReading.setOldIndex(meter.getClosestIndex());
        meterReading.setQuantity(request.getNewIndex() - meter.getClosestIndex());
        meterReading.setReadingDate(LocalDate.now());

        if (request.getDescriptionMeterReading() == null
                || request.getDescriptionMeterReading().isBlank()) {
            meterReading.setDescriptionMeterReading("Ghi chỉ số tháng " + request.getMonth() + "/" + request.getYear());
        }

        meterReading.setCreatedAt(Instant.now());
        meterReading.setUpdatedAt(Instant.now());

        meterReadingRepository.save(meterReading);

        meter.setClosestIndex(request.getNewIndex());
        meter.setUpdatedAt(Instant.now());
        meterRepository.save(meter);

        return meterReadingMapper.toResponse(meterReading);
    }

    @Override
    public MeterReadingResponse updateMeterReading(String meterReadingId, MeterReadingUpdateRequest request) {
        MeterReading meterReading = meterReadingRepository
                .findById(meterReadingId)
                .orElseThrow(() -> new AppException(ErrorCode.METER_READING_NOT_FOUND));

        if (isReadingUsedInInvoice(meterReadingId)) {
            throw new AppException(ErrorCode.METER_READING_ALREADY_BILLED);
        }

        if (request.getNewIndex() < meterReading.getOldIndex()) {
            throw new AppException(ErrorCode.NEW_INDEX_LESS_THAN_OLD);
        }
        meterReadingMapper.toMeterReadingUpdate(meterReading, request);
        meterReading.setQuantity(request.getNewIndex() - meterReading.getOldIndex());
        meterReading.setUpdatedAt(Instant.now());

        Meter meter = meterRepository
                .findById(meterReading.getMeter().getId())
                .orElseThrow(() -> new AppException(ErrorCode.METER_NOT_FOUND));
        meter.setClosestIndex(request.getNewIndex());
        meter.setUpdatedAt(Instant.now());
        meterRepository.save(meter);

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
        return meterReadingRepository
                .findById(meterReadingId)
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

    public boolean isReadingUsedInInvoice(String meterReadingId) {
        MeterReading reading = meterReadingRepository
                .findById(meterReadingId)
                .orElseThrow(() -> new AppException(ErrorCode.METER_READING_NOT_FOUND));

        int oldIndex = reading.getOldIndex();
        int newIndex = reading.getNewIndex();
        int month = reading.getMonth();
        int year = reading.getYear();
        String meterId = reading.getMeter().getId();

        return invoiceDetailsRepository.existsByOldIndexAndNewIndexAndMonthAndYearAndMeterId(
                oldIndex, newIndex, month, year, meterId);
    }
}
