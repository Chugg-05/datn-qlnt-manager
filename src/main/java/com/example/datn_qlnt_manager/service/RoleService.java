package com.example.datn_qlnt_manager.service;

import com.example.datn_qlnt_manager.dto.PaginatedResponse;
import com.example.datn_qlnt_manager.dto.request.RoleRequest;
import com.example.datn_qlnt_manager.dto.response.RoleDetailResponse;

public interface RoleService {

    PaginatedResponse<RoleDetailResponse> filterRoles(String name, int page, int size);

    RoleDetailResponse createRole(RoleRequest request);

    RoleDetailResponse updateRole(String roleId, RoleRequest request);

    void deleteRole(String roleId);
}
