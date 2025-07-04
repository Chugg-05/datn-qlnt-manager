package com.example.datn_qlnt_manager.dto.response.building;

import com.example.datn_qlnt_manager.dto.response.floor.FloorSelectResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BuildingSelectResponse {

    private String id;
    private String name;
    List<FloorSelectResponse> floors;
}
