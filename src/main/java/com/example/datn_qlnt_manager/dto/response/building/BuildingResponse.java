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
public class BuildingResponse {
    String id;
    String fullName;
    String buildingCode;
    String buildingName;
    String address;
    Integer actualNumberOfFloors;
    Integer numberOfFloorsForRent;
    BuildingType buildingType;
    BuildingStatus status;
    String description;
}