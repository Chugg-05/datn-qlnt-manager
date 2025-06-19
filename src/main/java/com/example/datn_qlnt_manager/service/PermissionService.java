package com.example.datn_qlnt_manager.service;

import com.example.datn_qlnt_manager.dto.PaginatedResponse;
import com.example.datn_qlnt_manager.dto.request.PermissionRequest;
import com.example.datn_qlnt_manager.dto.response.PermissionResponse;

public interface PermissionService {
    PaginatedResponse<PermissionResponse> filterPermissions(String name, int page, int size);

    PermissionResponse createPermission(PermissionRequest request);

    PermissionResponse updatePermission(String permissionId, PermissionRequest request);

    void deletePermission(String permissionId);
}
