package com.example.datn_qlnt_manager.service;

import java.util.List;

import com.example.datn_qlnt_manager.dto.request.PermissionRequest;
import com.example.datn_qlnt_manager.dto.response.PermissionResponse;

public interface PermissionService {
    PermissionResponse createPermission(PermissionRequest request);

    List<PermissionResponse> getPermissions();

    PermissionResponse updatePermission(String permissionId, PermissionRequest request);

    void deletePermission(String permissionId);
}
