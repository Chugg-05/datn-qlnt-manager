package com.example.datn_qlnt_manager.service;

import java.util.List;

import com.example.datn_qlnt_manager.dto.request.RoleRequest;
import com.example.datn_qlnt_manager.dto.response.RoleResponse;

public interface RoleService {
    List<RoleResponse> getRoles();

    RoleResponse createRole(RoleRequest request);

    RoleResponse updateRole(String roleId, RoleRequest request);

    void deleteRole(String roleId);
}
