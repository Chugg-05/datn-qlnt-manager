package com.example.datn_qlnt_manager.dto.response.building;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BuildingCountResponse {
    long totalBuilding;
    long activeBuilding;
    long inactiveBuilding;
}
