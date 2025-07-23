package com.example.datn_qlnt_manager.dto.response.building;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BuildingOccupancyResponse {
    String buildingId;
    String buildingName;
    Long totalRooms;
    Long occupiedRooms;
    Double occupancyRate;

    public BuildingOccupancyResponse(String buildingId, String buildingName, Long totalRooms, Long occupiedRooms) {
        this.buildingId = buildingId;
        this.buildingName = buildingName;
        this.totalRooms = totalRooms;
        this.occupiedRooms = occupiedRooms;
        this.occupancyRate = (totalRooms == 0) ? 0.0 : (occupiedRooms * 100.0 / totalRooms);
    }
}
