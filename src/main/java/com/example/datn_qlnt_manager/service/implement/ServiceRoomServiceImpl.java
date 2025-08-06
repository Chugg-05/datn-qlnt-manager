package com.example.datn_qlnt_manager.service.implement;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.example.datn_qlnt_manager.common.*;
import com.example.datn_qlnt_manager.dto.response.IdNameAndType;
import com.example.datn_qlnt_manager.dto.projection.ServiceRoomView;
import com.example.datn_qlnt_manager.dto.request.service.ServiceUpdateUnitPriceRequest;
import com.example.datn_qlnt_manager.dto.request.serviceRoom.ServiceRoomCreationForBuildingRequest;
import com.example.datn_qlnt_manager.dto.request.serviceRoom.ServiceRoomCreationForServiceRequest;
import com.example.datn_qlnt_manager.dto.request.serviceRoom.ServiceRoomCreationRequest;
import com.example.datn_qlnt_manager.dto.response.room.RoomBasicResponse;
import com.example.datn_qlnt_manager.dto.response.service.ServiceDetailResponse;
import com.example.datn_qlnt_manager.dto.response.service.ServiceUpdateUnitPriceResponse;
import com.example.datn_qlnt_manager.dto.response.serviceRoom.ServiceRoomDetailResponse;
import com.example.datn_qlnt_manager.dto.response.service.ServiceLittleResponse;
import com.example.datn_qlnt_manager.dto.response.serviceRoom.ServiceRoomResponse;
import com.example.datn_qlnt_manager.entity.*;
import com.example.datn_qlnt_manager.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.example.datn_qlnt_manager.dto.PaginatedResponse;
import com.example.datn_qlnt_manager.dto.filter.ServiceRoomFilter;
import com.example.datn_qlnt_manager.dto.request.serviceRoom.ServiceRoomCreationForRoomRequest;
import com.example.datn_qlnt_manager.dto.response.serviceRoom.CreateRoomServiceInitResponse;
import com.example.datn_qlnt_manager.dto.statistics.ServiceRoomStatistics;
import com.example.datn_qlnt_manager.exception.AppException;
import com.example.datn_qlnt_manager.exception.ErrorCode;
import com.example.datn_qlnt_manager.mapper.ServiceRoomMapper;
import com.example.datn_qlnt_manager.service.ServiceRoomService;
import com.example.datn_qlnt_manager.service.UserService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.transaction.annotation.Transactional;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ServiceRoomServiceImpl implements ServiceRoomService {

    ServiceRoomRepository serviceRoomRepository;
    BuildingRepository buildingRepository;
    ContractRepository contractRepository;
    RoomRepository roomRepository;
    ServiceRepository serviceRepository;
    ServiceRoomMapper serviceRoomMapper;
    UserService userService;

    @Override
    public PaginatedResponse<ServiceRoomView> getServiceRoomsPaging(ServiceRoomFilter filter, int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(0, page - 1), size);
        User user = userService.getCurrentUser();

        Page<ServiceRoomView> paging = serviceRoomRepository.getServiceRoomsPaging(
                user.getId(),
                filter.getQuery(),
                filter.getBuilding(),
                filter.getFloor(),
                filter.getRoomType(),
                filter.getStatus(),
                pageable);

        List<ServiceRoomView> responses = paging.getContent();

        Meta<?> meta = Meta.builder()
                .pagination(Pagination.builder()
                        .count(paging.getNumberOfElements())
                        .perPage(size)
                        .currentPage(page)
                        .totalPages(paging.getTotalPages())
                        .total(paging.getTotalElements())
                        .build())
                .build();

        return PaginatedResponse.<ServiceRoomView>builder()
                .data(responses)
                .meta(meta)
                .build();
    }

    @Override
    public ServiceRoomDetailResponse getServiceRoomDetail(String roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_FOUND));

        List<ServiceRoom> serviceRooms = serviceRoomRepository.findAllByRoomWithService(room);

        List<ServiceLittleResponse> services = serviceRooms.stream()
                .map(sr -> ServiceLittleResponse.builder()
                        .id(sr.getId())
                        .serviceName(sr.getService().getName())
                        .unitPrice(sr.getUnitPrice())
                        .unit(sr.getService().getUnit())
                        .serviceRoomStatus(sr.getServiceRoomStatus())
                        .description(sr.getDescription())
                        .build())
                .toList();

        return ServiceRoomDetailResponse.builder()
                .id(room.getId())
                .roomCode(room.getRoomCode())
                .roomType(room.getRoomType())
                .status(room.getStatus())
                .description(room.getDescription())
                .services(services)
                .build();
    }

    @Transactional
    @Override
    public ServiceRoomDetailResponse createRoomServiceForRoom(ServiceRoomCreationForRoomRequest request) {
        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_FOUND));

        List<Service> services = serviceRepository.findAllById(request.getServiceIds());
        if (services.size() != request.getServiceIds().size()) {
            throw new AppException(ErrorCode.SERVICE_NOT_FOUND);
        }

        for (Service service : services) {
            if (service.getStatus() != ServiceStatus.HOAT_DONG) {
                throw new AppException(ErrorCode.SERVICE_NOT_ACTIVE);
            }
            assignServiceToRooms(service, List.of(room));
        }

        List<ServiceRoom> serviceRooms = serviceRoomRepository.findAllByRoomWithService(room);
        List<ServiceLittleResponse> serviceResponses = serviceRooms.stream()
                .map(serviceRoomMapper::toServiceLittleResponse)
                .toList();

        return serviceRoomMapper.toServiceRoomDetailResponse(room, serviceResponses);
    }

    @Transactional
    @Override
    public ServiceDetailResponse createRoomServiceForService(ServiceRoomCreationForServiceRequest request) {
        Service service = serviceRepository.findById(request.getServiceId())
                .orElseThrow(() -> new AppException(ErrorCode.SERVICE_NOT_FOUND));

        if (service.getStatus() != ServiceStatus.HOAT_DONG) {
            throw new AppException(ErrorCode.SERVICE_NOT_ACTIVE);
        }

        List<Room> rooms = roomRepository.findAllById(request.getRoomIds());
        if (rooms.size() != request.getRoomIds().size()) {
            throw new AppException(ErrorCode.ROOM_NOT_FOUND);
        }

        assignServiceToRooms(service, rooms);

        List<ServiceRoom> serviceRooms = serviceRoomRepository.findAllByServiceWithRoom(service.getId());
        List<RoomBasicResponse> roomResponses = serviceRooms.stream()
                .map(sr -> serviceRoomMapper.toRoomBasicResponse(sr.getRoom()))
                .toList();

        return serviceRoomMapper.toServiceSmallResponse(service, roomResponses);
    }

    @Transactional
    @Override
    public ServiceDetailResponse createRoomServiceForBuilding(ServiceRoomCreationForBuildingRequest request) {
        Service service = serviceRepository.findById(request.getServiceId())
                .orElseThrow(() -> new AppException(ErrorCode.SERVICE_NOT_FOUND));

        if (service.getStatus() != ServiceStatus.HOAT_DONG) {
            throw new AppException(ErrorCode.SERVICE_NOT_ACTIVE);
        }

        List<Room> rooms = roomRepository.findByFloorBuildingId(request.getBuildingId());

        if (rooms.isEmpty()) {
            throw new AppException(ErrorCode.ROOM_NOT_FOUND_IN_BUILDING);
        }

        assignServiceToRooms(service, rooms);

        List<ServiceRoom> serviceRooms = serviceRoomRepository.findAllByServiceWithRoom(service.getId());
        List<RoomBasicResponse> roomResponses = serviceRooms.stream()
                .map(sr -> serviceRoomMapper.toRoomBasicResponse(sr.getRoom()))
                .toList();

        return serviceRoomMapper.toServiceSmallResponse(service, roomResponses);
    }

    @Transactional
    @Override
    public ServiceRoomResponse createServiceRoom(ServiceRoomCreationRequest request) {
        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_FOUND));

        Service service = serviceRepository.findById(request.getServiceId())
                .orElseThrow(() -> new AppException(ErrorCode.SERVICE_NOT_FOUND));

        if (service.getStatus() != ServiceStatus.HOAT_DONG) {
            throw new AppException(ErrorCode.SERVICE_NOT_ACTIVE);
        }

        boolean exists = serviceRoomRepository.existsByRoomAndService(room, service);
        if (exists) {
            throw new AppException(ErrorCode.SERVICE_ROOM_ALREADY_EXISTS);
        }

        ServiceRoom serviceRoom = ServiceRoom.builder()
                .room(room)
                .service(service)
                .unitPrice(service.getPrice())
                .startDate(LocalDate.now())
                .serviceRoomStatus(ServiceRoomStatus.DANG_SU_DUNG)
                .description(service.getName() + " đã được thêm vào phòng " + room.getRoomCode())
                .build();

        serviceRoom.setCreatedAt(Instant.now());
        serviceRoom.setUpdatedAt(Instant.now());

        return serviceRoomMapper.toServiceRoomResponse(serviceRoomRepository.save(serviceRoom));
    }

    @Transactional
    @Override
    public ServiceUpdateUnitPriceResponse updateServicePriceInBuilding(ServiceUpdateUnitPriceRequest request) {
        Service service = serviceRepository.findById(request.getServiceId())
                .orElseThrow(() -> new AppException(ErrorCode.SERVICE_NOT_FOUND));

        Building building = buildingRepository.findById(request.getBuildingId())
                .orElseThrow(() -> new AppException(ErrorCode.BUILDING_NOT_FOUND));

        List<ServiceRoom> serviceRooms = serviceRoomRepository
                .findAllByServiceAndBuilding(service.getId(), building.getId());

        int updatedCount = 0;

        for (ServiceRoom sr : serviceRooms) {

            if (service.getServiceCategory() == ServiceCategory.DIEN ||
                    service.getServiceCategory() == ServiceCategory.NUOC) {

                Optional<Contract> contractOpt = contractRepository.findActiveContractByRoomId(sr.getRoom().getId());

                if (contractOpt.isPresent()) {
                    Contract contract = contractOpt.get();

                    // Chỉ skip đúng loại dịch vụ
                    if (service.getServiceCategory() == ServiceCategory.DIEN && contract.getElectricPrice() != null) {
                        continue;
                    }

                    if (service.getServiceCategory() == ServiceCategory.NUOC && contract.getWaterPrice() != null) {
                        continue;
                    }
                }
            }

            service.setPrice(request.getNewUnitPrice());
            service.setUpdatedAt(Instant.now());
            sr.setUnitPrice(request.getNewUnitPrice());
            sr.setUpdatedAt(Instant.now());
            updatedCount++;
        }

        if (updatedCount > 0) {
            serviceRoomRepository.saveAll(serviceRooms);
        }

        return ServiceUpdateUnitPriceResponse.builder()
                .totalUpdated(updatedCount)
                .newUnitPrice(request.getNewUnitPrice())
                .serviceName(service.getName())
                .buildingName(building.getBuildingName())
                .build();
    }

    @Override
    public ServiceRoomStatistics getServiceRoomStatusStatistics(String buildingId) {
        User user = userService.getCurrentUser();
        return serviceRoomRepository.countByStatus(user.getId(), buildingId);
    }

    @Override
    public void toggleServiceRoomStatus(String id) {
        ServiceRoom serviceRoom = serviceRoomRepository
                .findByIdAndServiceRoomStatusNot(id, ServiceRoomStatus.DA_HUY)
                .orElseThrow(() -> new AppException(ErrorCode.SERVICE_ROOM_NOT_FOUND));

        if (serviceRoom.getServiceRoomStatus() == ServiceRoomStatus.DANG_SU_DUNG) {
            serviceRoom.setServiceRoomStatus(ServiceRoomStatus.TAM_DUNG);
            serviceRoom.setEndDate(LocalDate.now());
            serviceRoom.setUpdatedAt(Instant.now());
        } else if (serviceRoom.getServiceRoomStatus() == ServiceRoomStatus.TAM_DUNG) {
            serviceRoom.setServiceRoomStatus(ServiceRoomStatus.DANG_SU_DUNG);
            serviceRoom.setEndDate(null);
            serviceRoom.setUpdatedAt(Instant.now());
        } else {
            throw new AppException(ErrorCode.CANNOT_TOGGLE_SERVICE_STATUS);
        }
        serviceRoomRepository.save(serviceRoom);
    }

    @Override
    public void deleteServiceRoom(String serviceRoomId) {
        ServiceRoom serviceRoom = serviceRoomRepository
                .findById(serviceRoomId)
                .orElseThrow(() -> new AppException(ErrorCode.SERVICE_ROOM_NOT_FOUND));

        Service service = serviceRoom.getService();

        if (
                service.getServiceCategory() == ServiceCategory.NUOC
                        || service.getServiceCategory() == ServiceCategory.DIEN
        ) {
            throw new AppException(ErrorCode.SERVICE_ROOM_CANNOT_DELETE);
        }

        serviceRoomRepository.deleteById(serviceRoomId);
    }

    @Override
    public CreateRoomServiceInitResponse getServiceRoomInfoByUserId() {
        return CreateRoomServiceInitResponse.builder()
                .rooms(roomRepository.getServiceRoomInfoByUserId(
                        userService.getCurrentUser().getId()))
                .services(serviceRepository.findAllByUserId(
                        userService.getCurrentUser().getId()))
                .build();
    }

    @Override
    public List<IdNameAndType> getAllServiceRoomByUserId(String roomId) {
        return serviceRoomRepository.getAllServiceRoomByUserId(userService.getCurrentUser().getId(), roomId);
    }

    private void assignServiceToRooms(Service service, List<Room> rooms) {
        for (Room room : rooms) {
            if (serviceRoomRepository.existsByRoomAndService(room, service)) continue;

            ServiceRoom serviceRoom = ServiceRoom.builder()
                    .room(room)
                    .service(service)
                    .unitPrice(service.getPrice())
                    .startDate(LocalDate.now())
                    .serviceRoomStatus(ServiceRoomStatus.DANG_SU_DUNG)
                    .description(service.getName() + " đã được thêm vào phòng " + room.getRoomCode())
                    .build();

            serviceRoom.setCreatedAt(Instant.now());
            serviceRoom.setUpdatedAt(Instant.now());

            serviceRoomRepository.save(serviceRoom);
        }
    }



}
