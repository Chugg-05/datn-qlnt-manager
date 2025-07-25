package com.example.datn_qlnt_manager.dto.response.floor;

import java.time.Instant;

import com.example.datn_qlnt_manager.common.FloorType;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FloorResponse {
    String id;
    String nameFloor;
    Integer maximumRoom;
    FloorType floorType;
    String status;
    String buildingId;
    String buildingName;
    String descriptionFloor;
    Instant createdAt;
    Instant updatedAt;
}
