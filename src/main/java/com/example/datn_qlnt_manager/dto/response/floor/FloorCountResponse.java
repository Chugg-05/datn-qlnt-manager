package com.example.datn_qlnt_manager.dto.response.floor;

import lombok.*;
import lombok.experimental.FieldDefaults;

@NoArgsConstructor
@AllArgsConstructor
@Data

@FieldDefaults(level = AccessLevel.PRIVATE)
public class FloorCountResponse {
    String buildingId;
    Long totalFloors;
    Long activeFloors;
    Long inactiveFloors;
}
