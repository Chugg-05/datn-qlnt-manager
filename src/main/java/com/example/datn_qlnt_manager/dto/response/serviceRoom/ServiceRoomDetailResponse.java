package com.example.datn_qlnt_manager.dto.response.serviceRoom;

import com.example.datn_qlnt_manager.common.RoomStatus;
import com.example.datn_qlnt_manager.common.RoomType;
import com.example.datn_qlnt_manager.dto.response.service.ServiceLittleResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ServiceRoomDetailResponse {
    String id;
    String roomCode;
    RoomType roomType;
    RoomStatus status;
    String description;
    List<ServiceLittleResponse> services;
}
