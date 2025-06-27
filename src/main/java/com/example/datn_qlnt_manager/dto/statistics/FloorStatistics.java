package com.example.datn_qlnt_manager.dto.statistics;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@NoArgsConstructor
@AllArgsConstructor
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FloorStatistics {
    String buildingId;
    Long totalFloors;
    Long activeFloors;
    Long inactiveFloors;
}
