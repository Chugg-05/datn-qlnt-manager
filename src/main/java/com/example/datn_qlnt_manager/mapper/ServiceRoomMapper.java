package com.example.datn_qlnt_manager.mapper;

import org.mapstruct.*;

import com.example.datn_qlnt_manager.dto.request.serviceRoom.ServiceRoomCreationRequest;
import com.example.datn_qlnt_manager.dto.request.serviceRoom.ServiceRoomUpdateRequest;
import com.example.datn_qlnt_manager.dto.response.serviceRoom.ServiceRoomResponse;
import com.example.datn_qlnt_manager.entity.Room;
import com.example.datn_qlnt_manager.entity.Service;
import com.example.datn_qlnt_manager.entity.ServiceRoom;

@Mapper(componentModel = "spring")
public interface ServiceRoomMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "serviceRoomStatus", constant = "DANG_SU_DUNG")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "room", source = "room")
    @Mapping(target = "service", source = "service")
    ServiceRoom toServiceRoom(ServiceRoomCreationRequest request, Room room, Service service);

    @Mapping(target = "roomId", source = "room.id")
    @Mapping(target = "roomCode", source = "room.roomCode")
    @Mapping(target = "serviceId", source = "service.id")
    @Mapping(target = "name", source = "service.name")
    ServiceRoomResponse toResponse(ServiceRoom serviceRoom);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateServiceRoom(ServiceRoomUpdateRequest request, @MappingTarget ServiceRoom serviceRoom);
}
