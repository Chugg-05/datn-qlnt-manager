package com.example.datn_qlnt_manager.dto.response.room;

import java.util.List;

import com.example.datn_qlnt_manager.dto.response.tenant.TenantSelectResponse;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoomSelectResponse {

    private String id;
    private String name;
    private List<TenantSelectResponse> tenants;
}
