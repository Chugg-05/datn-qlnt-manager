package com.example.datn_qlnt_manager.service;

import com.example.datn_qlnt_manager.dto.PaginatedResponse;
import com.example.datn_qlnt_manager.dto.request.RoleRequest;
import com.example.datn_qlnt_manager.dto.response.RoleResponse;

public interface RoleService {
    PaginatedResponse<RoleResponse> filterRoles(String name, int page, int size);

    RoleResponse createRole(RoleRequest request);

    RoleResponse updateRole(String roleId, RoleRequest request);

    void deleteRole(String roleId);
}
