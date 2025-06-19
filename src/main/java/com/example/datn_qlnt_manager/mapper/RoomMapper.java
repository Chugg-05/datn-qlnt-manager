package com.example.datn_qlnt_manager.mapper;


import com.example.datn_qlnt_manager.dto.request.room.RoomCreationRequest;
import com.example.datn_qlnt_manager.dto.request.room.RoomDeleteRequest;
import com.example.datn_qlnt_manager.dto.request.room.RoomUpdateRequest;
import org.mapstruct.Mapper;

import com.example.datn_qlnt_manager.dto.response.room.RoomResponse;
import com.example.datn_qlnt_manager.entity.Room;

@Mapper(componentModel = "spring")
public interface RoomMapper {

    RoomResponse toRoomResponse(Room room);
    Room toRoomCreation(RoomCreationRequest request);
    Room toRoomUpdate(RoomUpdateRequest request);
}
