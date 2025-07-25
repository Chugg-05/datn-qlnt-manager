package com.example.datn_qlnt_manager.dto.response.building;

import com.example.datn_qlnt_manager.common.BuildingStatus;
import com.example.datn_qlnt_manager.common.BuildingType;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BuildingBasicResponse {
    String id;
    String buildingName;
    String address;
    BuildingType buildingType;
    BuildingStatus status;
    Long totalRoomAvail;
    Long totalRoom;
}
