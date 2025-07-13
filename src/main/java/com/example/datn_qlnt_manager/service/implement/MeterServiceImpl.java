package com.example.datn_qlnt_manager.service.implement;

import com.example.datn_qlnt_manager.common.Meta;
import com.example.datn_qlnt_manager.common.Pagination;
import com.example.datn_qlnt_manager.dto.PaginatedResponse;
import com.example.datn_qlnt_manager.dto.filter.MeterFilter;
import com.example.datn_qlnt_manager.dto.request.meter.MeterCreationRequest;
import com.example.datn_qlnt_manager.dto.request.meter.MeterUpdateRequest;
import com.example.datn_qlnt_manager.dto.response.meter.MeterReadingMonthlyStatsResponse;
import com.example.datn_qlnt_manager.dto.response.meter.MeterResponse;
import com.example.datn_qlnt_manager.entity.Meter;
import com.example.datn_qlnt_manager.entity.Room;
import com.example.datn_qlnt_manager.exception.AppException;
import com.example.datn_qlnt_manager.exception.ErrorCode;
import com.example.datn_qlnt_manager.mapper.MeterMapper;
import com.example.datn_qlnt_manager.repository.MeterReadingRepository;
import com.example.datn_qlnt_manager.repository.MeterRepository;
import com.example.datn_qlnt_manager.repository.RoomRepository;
import com.example.datn_qlnt_manager.repository.ServiceRepository;
import com.example.datn_qlnt_manager.service.MeterService;
import com.example.datn_qlnt_manager.service.UserService;
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
    MeterReadingRepository meterReadingRepository;
    MeterMapper meterMapper;
    RoomRepository roomRepository;
    ServiceRepository serviceRepository;
    UserService userService;


    @Override
    public PaginatedResponse<MeterResponse> getPageAndSearchAndFilterMeterByUserId(MeterFilter meterFilter, int page, int size) {
        String currentUserId = userService.getCurrentUser().getId();

        Pageable pageable = PageRequest.of(Math.max(0, page - 1), size, Sort.by(Sort.Direction.DESC, "updatedAt"));

        Page<Meter> pageResult = meterRepository.findByUserIdWithFilter(
                currentUserId,
                meterFilter.getBuildingId(),
                meterFilter.getRoomCode(),
                meterFilter.getMeterType(),
                meterFilter.getQuery(),
                pageable
        );

        Meta<?> meta = Meta.builder()
                .pagination(Pagination.builder()
                        .count(pageResult.getNumberOfElements())
                        .perPage(size)
                        .currentPage(page)
                        .totalPages(pageResult.getTotalPages())
                        .total(pageResult.getTotalElements())
                        .build())
                .build();

        List<MeterResponse> responseList = pageResult.map(meterMapper::toMeterResponse).getContent();

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

        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_FOUND));

        com.example.datn_qlnt_manager.entity.Service service = serviceRepository.findById(request.getServiceId())
                .orElseThrow(() -> new AppException(ErrorCode.SERVICE_NOT_FOUND));

        Meter meter = meterMapper.toMeterCreation(request);
        meter.setRoom(room);
        meter.setService(service);
        meter.setCreatedAt(Instant.now());
        meter.setUpdatedAt(Instant.now());

        return meterMapper.toMeterResponse(meterRepository.save(meter));
    }

    @Override
    public MeterResponse updateMeter(String meterId, MeterUpdateRequest request) {
        Meter meter = meterRepository.findById(meterId)
                .orElseThrow(() -> new AppException(ErrorCode.METER_NOT_FOUND));

        // check trùng mã
        if (!meter.getMeterCode().equalsIgnoreCase(request.getMeterCode())) {
            boolean isExist = meterRepository.existsByMeterCode(request.getMeterCode());
            if (isExist) {
                throw new AppException(ErrorCode.METER_CODE_EXISTED);
            }
            meter.setMeterCode(request.getMeterCode());
        }
        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_FOUND));

        com.example.datn_qlnt_manager.entity.Service service = serviceRepository.findById(request.getServiceId())
                .orElseThrow(() -> new AppException(ErrorCode.SERVICE_NOT_FOUND));

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
    public List<MeterReadingMonthlyStatsResponse> getMonthlyStats(String meterCode) {
        return meterReadingRepository.getMonthlyStats(meterCode);
    }


}
