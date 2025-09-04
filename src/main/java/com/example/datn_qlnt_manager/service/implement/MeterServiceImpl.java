package com.example.datn_qlnt_manager.service.implement;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import com.example.datn_qlnt_manager.common.*;
import com.example.datn_qlnt_manager.dto.request.meter.ChangeMeterRequest;
import com.example.datn_qlnt_manager.entity.MeterReading;
import jakarta.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.example.datn_qlnt_manager.dto.PaginatedResponse;
import com.example.datn_qlnt_manager.dto.filter.MeterFilter;
import com.example.datn_qlnt_manager.dto.filter.MeterInitFilterResponse;
import com.example.datn_qlnt_manager.dto.request.meter.MeterCreationRequest;
import com.example.datn_qlnt_manager.dto.request.meter.MeterUpdateRequest;
import com.example.datn_qlnt_manager.dto.response.IdAndName;
import com.example.datn_qlnt_manager.dto.response.meter.CreateMeterInitResponse;
import com.example.datn_qlnt_manager.dto.response.meter.MeterReadingMonthlyStatsResponse;
import com.example.datn_qlnt_manager.dto.response.meter.MeterResponse;
import com.example.datn_qlnt_manager.dto.response.meter.RoomNoMeterResponse;
import com.example.datn_qlnt_manager.dto.statistics.RoomNoMeterCountStatistics;
import com.example.datn_qlnt_manager.entity.Meter;
import com.example.datn_qlnt_manager.entity.Room;
import com.example.datn_qlnt_manager.entity.User;
import com.example.datn_qlnt_manager.exception.AppException;
import com.example.datn_qlnt_manager.exception.ErrorCode;
import com.example.datn_qlnt_manager.mapper.MeterMapper;
import com.example.datn_qlnt_manager.repository.*;
import com.example.datn_qlnt_manager.service.MeterService;
import com.example.datn_qlnt_manager.service.UserService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@Transactional
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MeterServiceImpl implements MeterService {

    MeterRepository meterRepository;
    MeterReadingRepository meterReadingRepository;
    MeterMapper meterMapper;
    RoomRepository roomRepository;
    ServiceRepository serviceRepository;
    UserService userService;

    @Override
    public PaginatedResponse<MeterResponse> getPageAndSearchAndFilterMeterByUserId(
            MeterFilter meterFilter, int page, int size) {
        String currentUserId = userService.getCurrentUser().getId();

        Pageable pageable = PageRequest.of(Math.max(0, page - 1), size, Sort.by(Sort.Direction.DESC, "updatedAt"));

        Page<MeterResponse> pageResult = meterRepository.findByUserIdWithFilter(
                currentUserId,
                meterFilter.getBuildingId(),
                meterFilter.getRoomId(),
                meterFilter.getMeterType(),
                meterFilter.getQuery(),
                pageable);

        Meta<?> meta = Meta.builder()
                .pagination(Pagination.builder()
                        .count(pageResult.getNumberOfElements())
                        .perPage(size)
                        .currentPage(page)
                        .totalPages(pageResult.getTotalPages())
                        .total(pageResult.getTotalElements())
                        .build())
                .build();

        List<MeterResponse> responseList = pageResult.getContent();

        return PaginatedResponse.<MeterResponse>builder()
                .data(responseList)
                .meta(meta)
                .build();
    }

    @Override
    public MeterResponse createMeter(MeterCreationRequest request) {
        if (meterRepository.existsByMeterCode(request.getMeterCode())) {
            throw new AppException(ErrorCode.METER_CODE_EXISTED);
        }

        if (meterRepository.existsByRoomIdAndMeterName(request.getRoomId(), request.getMeterName())) {
            throw new AppException(ErrorCode.METER_NAME_ALREADY_EXISTS_IN_ROOM);
        }

        if (meterRepository.existsByRoomIdAndMeterType(request.getRoomId(), request.getMeterType())) {
            throw new AppException(ErrorCode.METER_TYPE_ALREADY_EXISTS_IN_ROOM);
        }

        Room room = roomRepository
                .findById(request.getRoomId())
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_FOUND));

        com.example.datn_qlnt_manager.entity.Service service = serviceRepository
                .findById(request.getServiceId())
                .orElseThrow(() -> new AppException(ErrorCode.SERVICE_NOT_FOUND));

        if ((service.getServiceCategory() == ServiceCategory.DIEN && request.getMeterType() != MeterType.DIEN)
                || (service.getServiceCategory() == ServiceCategory.NUOC && request.getMeterType() != MeterType.NUOC)) {
            throw new AppException(ErrorCode.METER_TYPE_NOT_MATCH_SERVICE);
        }

        Meter meter = meterMapper.toMeterCreation(request);
        meter.setRoom(room);
        meter.setService(service);
        meter.setCreatedAt(Instant.now());
        meter.setUpdatedAt(Instant.now());

        return meterMapper.toMeterResponse(meterRepository.save(meter));
    }

    @Override
    public MeterResponse updateMeter(String meterId, MeterUpdateRequest request) {
        Meter meter = meterRepository.findById(meterId).orElseThrow(() -> new AppException(ErrorCode.METER_NOT_FOUND));

        // check trùng mã
        if (!meter.getMeterCode().equalsIgnoreCase(request.getMeterCode())) {
            boolean isExist = meterRepository.existsByMeterCode(request.getMeterCode());
            if (isExist) {
                throw new AppException(ErrorCode.METER_CODE_EXISTED);
            }
            meter.setMeterCode(request.getMeterCode());
        }

        Room room = roomRepository
                .findById(request.getRoomId())
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_FOUND));

        com.example.datn_qlnt_manager.entity.Service service = serviceRepository
                .findById(request.getServiceId())
                .orElseThrow(() -> new AppException(ErrorCode.SERVICE_NOT_FOUND));

        if ((service.getServiceCategory() == ServiceCategory.DIEN && request.getMeterType() != MeterType.DIEN)
                || (service.getServiceCategory() == ServiceCategory.NUOC && request.getMeterType() != MeterType.NUOC)) {
            throw new AppException(ErrorCode.METER_TYPE_NOT_MATCH_SERVICE);
        }

        //update thông tin công tơ bên chỉ số
        List<MeterReading> meterReadings = meterReadingRepository.findAllByMeterCode(meter.getMeterCode());
        for (MeterReading meterReading : meterReadings) {
            meterReading.setMeterName(request.getMeterName());
            meterReading.setMeterCode(request.getMeterCode());
            meterReadingRepository.save(meterReading);
        }

        meterMapper.toMeterUpdate(meter, request);
        meter.setRoom(room);
        meter.setService(service);
        meter.setUpdatedAt(Instant.now());

        return meterMapper.toMeterResponse(meterRepository.save(meter));
    }

    @Override
    public void deleteMeter(String meterId) {
        if (!meterRepository.existsById(meterId)) {
            throw new AppException(ErrorCode.METER_NOT_FOUND);
        }
        meterRepository.deleteById(meterId);
    }

    @Override
    public List<MeterReadingMonthlyStatsResponse> getMonthlyStats(String roomId) {
        return meterReadingRepository.getMonthlyStats(
                roomId, userService.getCurrentUser().getId());
    }

    @Override
    public CreateMeterInitResponse getMeterInfoByUserId(String buildingId) {
        return CreateMeterInitResponse.builder()
                .rooms(roomRepository.getServiceRoomInfoByUserId(
                        userService.getCurrentUser().getId(), buildingId))
                .services(serviceRepository.getServiceInfoByUserId(
                        userService.getCurrentUser().getId()))
                .build();
    }

    @Override
    public MeterInitFilterResponse getMeterFilterByUserId(String buildingId) {
        return MeterInitFilterResponse.builder()
                .rooms(roomRepository.getRoomInfoByUserId(
                        userService.getCurrentUser().getId(), buildingId))
                .build();
    }

    @Override
    public List<IdAndName> findAllMeters() {
        return meterRepository.findAllByUserId(userService.getCurrentUser().getId());
    }

    @Override
    public PaginatedResponse<RoomNoMeterResponse> getRoomsWithoutMeterByUser(
            Integer page,
            Integer size,
            String query,
            RoomStatus status,
            RoomType roomType,
            BigDecimal minPrice,
            BigDecimal maxPrice) {

        User user = userService.getCurrentUser();
        Pageable pageable = PageRequest.of(Math.max(0, page - 1), size);

        Page<RoomNoMeterResponse> rooms = meterRepository.findRoomsWithoutMeterByUserIdWithFilter(
                user.getId(), query, status, roomType, minPrice, maxPrice, pageable);

        return PaginatedResponse.<RoomNoMeterResponse>builder()
                .data(rooms.getContent())
                .meta(Meta.builder()
                        .pagination(Pagination.builder()
                                .count(rooms.getNumberOfElements())
                                .perPage(size)
                                .currentPage(page)
                                .totalPages(rooms.getTotalPages())
                                .total(rooms.getTotalElements())
                                .build())
                        .build())
                .build();
    }

    @Override
    public RoomNoMeterCountStatistics countRoomsWithoutMeterByUser() {
        User user = userService.getCurrentUser();
        Long count = meterRepository.countRoomsWithoutMeterByUserId(user.getId());
        return new RoomNoMeterCountStatistics(count.intValue());
    }


    @Override
    public MeterResponse changeMeter(ChangeMeterRequest request, String meterId) {
        Meter meter = meterRepository.findById(meterId)
                .orElseThrow(() -> new AppException(ErrorCode.METER_NOT_FOUND));

        if (meterRepository.existsByMeterCode(request.getMeterCode())) {
            throw new AppException(ErrorCode.METER_CODE_EXISTED);
        }

        meter.setMeterName(request.getMeterName());
        meter.setMeterCode(request.getMeterCode());
        meter.setManufactureDate(request.getManufactureDate());
        meter.setClosestIndex(request.getClosestIndex());
        meter.setDescriptionMeter(request.getDescriptionMeter());
        meter.setCreatedAt(Instant.now());
        meter.setUpdatedAt(Instant.now());

        return meterMapper.toMeterResponse(meterRepository.save(meter));

    }
}
