package com.example.datn_qlnt_manager.service.implement;

import com.example.datn_qlnt_manager.common.Meta;
import com.example.datn_qlnt_manager.common.Pagination;
import com.example.datn_qlnt_manager.common.ServiceRoomStatus;
import com.example.datn_qlnt_manager.dto.PaginatedResponse;
import com.example.datn_qlnt_manager.dto.filter.ServiceRoomFilter;
import com.example.datn_qlnt_manager.dto.request.serviceRoom.ServiceRoomCreationRequest;
import com.example.datn_qlnt_manager.dto.request.serviceRoom.ServiceRoomUpdateRequest;
import com.example.datn_qlnt_manager.dto.response.serviceRoom.CreateRoomServiceInitResponse;
import com.example.datn_qlnt_manager.dto.response.serviceRoom.ServiceRoomResponse;
import com.example.datn_qlnt_manager.dto.statistics.ServiceRoomStatistics;
import com.example.datn_qlnt_manager.entity.Room;
import com.example.datn_qlnt_manager.entity.Service;
import com.example.datn_qlnt_manager.entity.ServiceRoom;
import com.example.datn_qlnt_manager.entity.User;
import com.example.datn_qlnt_manager.exception.AppException;
import com.example.datn_qlnt_manager.exception.ErrorCode;
import com.example.datn_qlnt_manager.mapper.ServiceRoomMapper;
import com.example.datn_qlnt_manager.repository.RoomRepository;
import com.example.datn_qlnt_manager.repository.ServiceRepository;
import com.example.datn_qlnt_manager.repository.ServiceRoomRepository;
import com.example.datn_qlnt_manager.service.ServiceRoomService;
import com.example.datn_qlnt_manager.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ServiceRoomServiceImpl implements ServiceRoomService {

    ServiceRoomRepository serviceRoomRepository;
    RoomRepository roomRepository;
    ServiceRepository serviceRepository;
    CodeGeneratorService codeGeneratorService;
    ServiceRoomMapper serviceRoomMapper;
    UserService userService;

    @Override
    public ServiceRoomResponse createServiceRoom(ServiceRoomCreationRequest request) {
        Room room =
                roomRepository.findById(request.getRoomId()).orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_FOUND));

        Service service = serviceRepository.findById(request.getServiceId())
                .orElseThrow(() -> new AppException(ErrorCode.SERVICE_NOT_FOUND));


        if (serviceRoomRepository.existsByRoomIdAndServiceId(room.getId(), service.getId())) {
            throw new AppException(ErrorCode.ROOM_EXISTED_SERVICE);
        }
        ServiceRoom serviceRoom = serviceRoomMapper.toServiceRoom(request, room, service);
        serviceRoom.setApplyTime(LocalDateTime.now());
        serviceRoom.setCreatedAt(Instant.now());
        serviceRoom.setUpdatedAt(Instant.now());
        serviceRoom.setUsageCode(codeGeneratorService.generateServiceRoomCode(room));
        return serviceRoomMapper.toResponse(serviceRoomRepository.save(serviceRoom));
    }

    @Override
    public ServiceRoomResponse updateServiceRoom(String serviceRoomId, ServiceRoomUpdateRequest request) {
        ServiceRoom serviceRoom =
                serviceRoomRepository.findById(serviceRoomId).orElseThrow(() -> new AppException(ErrorCode.SERVICE_ROOM_NOT_FOUND));

        serviceRoomMapper.updateServiceRoom(request, serviceRoom);
        serviceRoom.setUpdatedAt(Instant.now());
        return serviceRoomMapper.toResponse(serviceRoomRepository.save(serviceRoom));
    }

    @Override
    public void softDeleteServiceRoom(String serviceRoomId) {
        ServiceRoom serviceRoom = serviceRoomRepository.findByIdAndServiceRoomStatusNot(serviceRoomId,
                        ServiceRoomStatus.DA_HUY)
                .orElseThrow(() -> new AppException(ErrorCode.SERVICE_ROOM_NOT_FOUND));

        serviceRoom.setServiceRoomStatus(ServiceRoomStatus.DA_HUY);
        serviceRoomRepository.save(serviceRoom);
    }

    @Override
    public void deleteServiceRoom(String serviceRoomId) {
        if (!serviceRoomRepository.existsById(serviceRoomId)) {
            throw new AppException(ErrorCode.SERVICE_ROOM_NOT_FOUND);

        }
        serviceRoomRepository.deleteById(serviceRoomId);
    }

    @Override
    public PaginatedResponse<ServiceRoomResponse> filterServiceRooms(ServiceRoomFilter filter, int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(0, page - 1), size, Sort.by(Sort.Direction.DESC, "updatedAt"));
        User currentUser = userService.getCurrentUser();

        Page<ServiceRoom> serviceRoomPage = serviceRoomRepository.filterServiceRoomsPaging(
                currentUser.getId(),
                filter.getQuery(),
                filter.getMinPrice(),
                filter.getMaxPrice(),
                filter.getStatus(),
                pageable
        );

        List<ServiceRoomResponse> responses = serviceRoomPage.getContent().stream()
                .map(serviceRoomMapper::toResponse)
                .toList();

        Meta<?> meta = Meta.builder()
                .pagination(Pagination.builder()
                        .count(serviceRoomPage.getNumberOfElements())
                        .perPage(size)
                        .currentPage(page)
                        .totalPages(serviceRoomPage.getTotalPages())
                        .total(serviceRoomPage.getTotalElements())
                        .build())
                .build();

        return PaginatedResponse.<ServiceRoomResponse>builder()
                .data(responses)
                .meta(meta)
                .build();
    }

    @Override
    public ServiceRoomStatistics getServiceRoomStatusStatistics() {
        User user = userService.getCurrentUser();
        return serviceRoomRepository.countByStatus(user.getId());
    }

    @Override
    public void toggleServiceRoomStatus(String id) {
        ServiceRoom serviceRoom = serviceRoomRepository
                .findByIdAndServiceRoomStatusNot(id, ServiceRoomStatus.DA_HUY)
                .orElseThrow(() -> new AppException(ErrorCode.SERVICE_ROOM_NOT_FOUND));

        if (serviceRoom.getServiceRoomStatus() == ServiceRoomStatus.DANG_SU_DUNG) {
            serviceRoom.setServiceRoomStatus(ServiceRoomStatus.TAM_DUNG);
            serviceRoom.setUpdatedAt(Instant.now());
        } else if (serviceRoom.getServiceRoomStatus() == ServiceRoomStatus.TAM_DUNG) {
            serviceRoom.setServiceRoomStatus(ServiceRoomStatus.DANG_SU_DUNG);
            serviceRoom.setUpdatedAt(Instant.now());
        } else {
            throw new AppException(ErrorCode.SERVICE_ROOM_NOT_FOUND);
        }
        serviceRoomRepository.save(serviceRoom);
    }

    @Override
    public CreateRoomServiceInitResponse getServiceRoomInfoByUserId() {
        return CreateRoomServiceInitResponse.builder()
                .rooms(roomRepository.getServiceRoomInfoByUserId(userService.getCurrentUser().getId()))
                .services(serviceRepository.findAllByUserId(userService.getCurrentUser().getId()))
                .build();
    }

}
