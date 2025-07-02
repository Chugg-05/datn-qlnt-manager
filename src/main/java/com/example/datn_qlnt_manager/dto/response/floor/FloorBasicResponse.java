package com.example.datn_qlnt_manager.dto.response.floor;

import com.example.datn_qlnt_manager.common.FloorType;
import com.example.datn_qlnt_manager.common.FloorStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FloorBasicResponse {
//    String floorId;
    String id;
    String nameFloor;
    FloorType floorType;
    FloorStatus status;
    Integer maximumRoom;
    String buildingName;
}
