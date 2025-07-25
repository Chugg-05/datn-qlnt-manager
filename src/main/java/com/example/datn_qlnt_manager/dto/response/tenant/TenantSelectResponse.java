package com.example.datn_qlnt_manager.dto.response.tenant;

import com.example.datn_qlnt_manager.dto.response.IdAndName;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TenantSelectResponse extends IdAndName {

    public TenantSelectResponse(String id, String name) {
        super(id, name);
    }
}
