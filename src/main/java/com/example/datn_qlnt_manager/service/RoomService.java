package com.example.datn_qlnt_manager.service;

import java.util.List;

import com.example.datn_qlnt_manager.common.RoomStatus;
import com.example.datn_qlnt_manager.dto.PaginatedResponse;
import com.example.datn_qlnt_manager.dto.filter.RoomFilter;
import com.example.datn_qlnt_manager.dto.request.room.RoomCreationRequest;
import com.example.datn_qlnt_manager.dto.request.room.RoomUpdateRequest;
import com.example.datn_qlnt_manager.dto.response.room.RoomCountResponse;
import com.example.datn_qlnt_manager.dto.response.room.RoomDetailsResponse;
import com.example.datn_qlnt_manager.dto.response.room.RoomResponse;
import com.example.datn_qlnt_manager.dto.statistics.RoomNoServiceStatisticResponse;
import com.example.datn_qlnt_manager.dto.statistics.RoomStatisticWithoutAssets;
import com.example.datn_qlnt_manager.dto.statistics.StatisticRoomsWithoutContract;
import com.example.datn_qlnt_manager.entity.Room;

public interface RoomService {


    PaginatedResponse<RoomResponse> getPageAndSearchAndFilterRoomByUserId(
            RoomFilter roomFilter, Integer page, Integer size);

    PaginatedResponse<RoomResponse> getRoomWithStatusCancelByUserId(RoomFilter roomFilter, Integer page, Integer size);

    List<RoomResponse> getRoomsByTenantId();

    RoomResponse createRoom(RoomCreationRequest request);

    RoomResponse updateRoom(String roomId, RoomUpdateRequest request);

    List<RoomResponse> getAllRoomsByUserId();

    Void deleteRoom(String roomId);

    void softDeleteRoomById(String id);

    RoomResponse updateRoomStatus(String roomId, RoomStatus status);

    RoomCountResponse statisticsRoomByStatus(String buildingId);

    PaginatedResponse<RoomResponse> getRoomsWithoutServiceByUserId(RoomFilter filter, Integer page, Integer size);

    StatisticRoomsWithoutContract statisticRoomsWithoutContractByUserId();

    RoomStatisticWithoutAssets statisticRoomsWithoutAssetByUserId(String buildingId);

    PaginatedResponse<RoomResponse> getRoomsWithoutAssets(String buildingId, Integer page, Integer size);

    List<RoomNoServiceStatisticResponse> getRoomNoServiceStatistic(String buildingId);

    RoomDetailsResponse getRoomDetails(String roomId);

    RoomResponse restoreRoomById(String roomId);

    List<RoomResponse> findRoomsByBuildingId(String buildingId);


}
