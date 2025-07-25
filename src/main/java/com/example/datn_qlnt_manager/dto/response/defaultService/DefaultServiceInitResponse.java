package com.example.datn_qlnt_manager.dto.response.defaultService;

import java.util.List;

import com.example.datn_qlnt_manager.dto.response.IdAndName;
import com.example.datn_qlnt_manager.dto.response.building.DefaultServiceBuildingSelectResponse;

import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DefaultServiceInitResponse {

    List<IdAndName> services;
    List<DefaultServiceBuildingSelectResponse> buildings;
}
