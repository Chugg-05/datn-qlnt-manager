package com.example.datn_qlnt_manager.service;

import com.example.datn_qlnt_manager.dto.PaginatedResponse;
import com.example.datn_qlnt_manager.dto.request.PermissionRequest;
import com.example.datn_qlnt_manager.dto.response.PermissionDetailResponse;

public interface PermissionService {

    PaginatedResponse<PermissionDetailResponse> filterPermissions(String name, int page, int size);

    PermissionDetailResponse createPermission(PermissionRequest request);

    PermissionDetailResponse updatePermission(String permissionId, PermissionRequest request);

    void deletePermission(String permissionId);
}
