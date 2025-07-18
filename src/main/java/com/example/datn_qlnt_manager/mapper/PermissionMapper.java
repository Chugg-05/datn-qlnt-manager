package com.example.datn_qlnt_manager.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import com.example.datn_qlnt_manager.dto.request.PermissionRequest;
import com.example.datn_qlnt_manager.dto.response.PermissionDetailResponse;
import com.example.datn_qlnt_manager.entity.Permission;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    Permission toPermission(PermissionRequest request);

    PermissionDetailResponse toPermissionDetailResponse(Permission permission);

    void updatePermission(PermissionRequest request, @MappingTarget Permission permission);
}
