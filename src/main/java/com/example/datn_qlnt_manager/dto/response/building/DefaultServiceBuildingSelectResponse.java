package com.example.datn_qlnt_manager.dto.response.building;

import java.util.List;

import com.example.datn_qlnt_manager.dto.response.floor.DefaultServiceFloorSelectResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DefaultServiceBuildingSelectResponse {
    private String id;
    private String name;
    List<DefaultServiceFloorSelectResponse> floors;
}
