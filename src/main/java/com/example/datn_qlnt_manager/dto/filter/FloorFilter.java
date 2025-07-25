package com.example.datn_qlnt_manager.dto.filter;

import jakarta.validation.constraints.Min;

import com.example.datn_qlnt_manager.common.FloorStatus;
import com.example.datn_qlnt_manager.common.FloorType;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FloorFilter {
    String buildingId;
    FloorStatus status;
    FloorType floorType;
    String nameFloor;

    @Min(value = 0, message = "MAX_ROOM_SEARCH")
    Integer maxRoom;
}
