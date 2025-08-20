package com.example.datn_qlnt_manager.mapper;

import org.mapstruct.*;

import com.example.datn_qlnt_manager.dto.request.assetRoom.AssetRoomUpdateRequest;
import com.example.datn_qlnt_manager.dto.response.assetRoom.AssetRoomResponse;
import com.example.datn_qlnt_manager.entity.AssetRoom;

@Mapper(componentModel = "spring")
public interface AssetRoomMapper {
    @Mapping(source = "room.roomCode", target = "roomCode")
    AssetRoomResponse toAssetRoomResponse(AssetRoom assetRoom);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateAssetRoomFromRequest(AssetRoomUpdateRequest request, @MappingTarget AssetRoom assetRoom);
}
