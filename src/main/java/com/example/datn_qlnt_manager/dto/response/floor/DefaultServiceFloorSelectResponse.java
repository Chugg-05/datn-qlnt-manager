package com.example.datn_qlnt_manager.dto.response.floor;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DefaultServiceFloorSelectResponse {
    private String id;
    private String name;
}
