package com.example.datn_qlnt_manager.mapper;

import java.util.List;

import org.mapstruct.*;

import com.example.datn_qlnt_manager.dto.response.room.RoomBasicResponse;
import com.example.datn_qlnt_manager.dto.response.service.ServiceDetailResponse;
import com.example.datn_qlnt_manager.dto.response.service.ServiceLittleResponse;
import com.example.datn_qlnt_manager.dto.response.serviceRoom.ServiceRoomDetailResponse;
import com.example.datn_qlnt_manager.dto.response.serviceRoom.ServiceRoomResponse;
import com.example.datn_qlnt_manager.entity.Room;
import com.example.datn_qlnt_manager.entity.Service;
import com.example.datn_qlnt_manager.entity.ServiceRoom;

@Mapper(componentModel = "spring")
public interface ServiceRoomMapper {
    @Mapping(source = "service.id", target = "id")
    @Mapping(source = "service.name", target = "serviceName")
    @Mapping(source = "unitPrice", target = "unitPrice")
    @Mapping(source = "service.unit", target = "unit")
    @Mapping(source = "serviceRoomStatus", target = "serviceRoomStatus")
    @Mapping(source = "description", target = "description")
    ServiceLittleResponse toServiceLittleResponse(ServiceRoom serviceRoom);

    ServiceRoomDetailResponse toServiceRoomDetailResponse(Room room, List<ServiceLittleResponse> services);

    RoomBasicResponse toRoomBasicResponse(Room room);

    ServiceDetailResponse toServiceSmallResponse(Service service, List<RoomBasicResponse> rooms);

    @Mapping(source = "room.roomCode", target = "roomCode")
    @Mapping(source = "service.name", target = "serviceName")
    ServiceRoomResponse toServiceRoomResponse(ServiceRoom serviceRoom);
}
