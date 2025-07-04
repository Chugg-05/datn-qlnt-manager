package com.example.datn_qlnt_manager.service.implement;

import com.example.datn_qlnt_manager.common.MeterType;
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
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@Transactional
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MeterReadingServiceImpl implements MeterReadingService {
    MeterReadingRepository meterReadingRepository;
    MeterRepository meterRepository;
    MeterReadingMapper meterReadingMapper;

    @Override
    public MeterReadingResponse createMeterReading(MeterReadingCreationRequest request) {
        Meter meter = meterRepository.findById(request.getMeterCode())
                .orElseThrow(() -> new AppException(ErrorCode.METER_NOT_FOUND));

        MeterReading meterReading = meterReadingMapper.toMeterReadingCreation(request);
        meterReading.setMeterCode(meter.getId());
        meterReading.setCreatedAt(Instant.now());
        meterReading.setUpdatedAt(Instant.now());

        return meterReadingMapper.toResponse(meterReadingRepository.save(meterReading));
    }

    @Override
    public MeterReadingResponse updateMeterReading(String id, MeterReadingUpdateRequest request) {
        MeterReading existing = meterReadingRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.METER_READING_NOT_FOUND));

        Meter meter = meterRepository.findById(request.getMeterCode())
                .orElseThrow(() -> new AppException(ErrorCode.METER_NOT_FOUND));

        MeterReading meterReading = meterReadingMapper.toMeterReadingUpdate(request);
        meterReading.setId(existing.getId());
        meterReading.setMeterCode(meter.getId());
        meterReading.setCreatedAt(existing.getCreatedAt());
        meterReading.setUpdatedAt(Instant.now());

        return meterReadingMapper.toResponse(meterReadingRepository.save(meterReading));
    }

    @Override
    public void deleteMeterReading(String id) {
        if (!meterReadingRepository.existsById(id)) {
            throw new AppException(ErrorCode.METER_READING_NOT_FOUND);
        }
        meterReadingRepository.deleteById(id);
    }

    @Override
    public MeterReadingResponse getMeterReadingById(String id) {
        return meterReadingRepository.findById(id)
                .map(meterReadingMapper::toResponse)
                .orElseThrow(() -> new AppException(ErrorCode.METER_READING_NOT_FOUND));
    }

    @Override
    public Page<MeterReadingResponse> filterMeterReadings(String buildingId, String roomCode, MeterType meterType, Integer month, Pageable pageable) {
        return meterReadingRepository.filterMeterReadings(buildingId, roomCode, meterType,month, pageable)
                .map(meterReadingMapper::toResponse);
    }
}
