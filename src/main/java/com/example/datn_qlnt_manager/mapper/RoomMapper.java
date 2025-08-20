package com.example.datn_qlnt_manager.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.datn_qlnt_manager.dto.request.room.RoomCreationRequest;
import com.example.datn_qlnt_manager.dto.request.room.RoomUpdateRequest;
import com.example.datn_qlnt_manager.dto.response.room.RoomResponse;
import com.example.datn_qlnt_manager.entity.Room;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface RoomMapper {

    @Mapping(source = "floor.building.id", target = "floor.buildingId")
    @Mapping(source = "floor.building.buildingName", target = "floor.buildingName")
    RoomResponse toRoomResponse(Room room);

    @Mapping(target = "roomCode", ignore = true)
    Room toRoomCreation(RoomCreationRequest request);

    @Mapping(target = "roomCode", ignore = true)
    @Mapping(target = "floor", ignore = true)
    @Mapping(target = "serviceRooms", ignore = true)
    @Mapping(target = "assetRooms", ignore = true)
    @Mapping(target = "contracts", ignore = true)
    void toRoomUpdate(RoomUpdateRequest request, @MappingTarget Room room);

}