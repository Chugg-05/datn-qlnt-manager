package com.example.datn_qlnt_manager.dto.filter;

import com.example.datn_qlnt_manager.common.BuildingStatus;
import com.example.datn_qlnt_manager.common.BuildingType;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BuildingFilter {
    String userId;
    String query;
    BuildingStatus status;
    BuildingType buildingType;
}
