package com.example.datn_qlnt_manager.mapper;

import org.mapstruct.Mapper;

import com.example.datn_qlnt_manager.dto.response.room.RoomResponse;
import com.example.datn_qlnt_manager.entity.Room;

@Mapper(componentModel = "spring")
public interface RoomMapper {

    RoomResponse toRoomResponse(Room room);
}
