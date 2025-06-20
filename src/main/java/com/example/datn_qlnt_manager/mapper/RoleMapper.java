package com.example.datn_qlnt_manager.mapper;

import com.example.datn_qlnt_manager.dto.response.RoleDetailResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.example.datn_qlnt_manager.dto.request.RoleRequest;
import com.example.datn_qlnt_manager.entity.Role;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    @Mapping(target = "permissions", ignore = true)
    Role toRole(RoleRequest request);

    RoleDetailResponse toRoleDetailResponse(Role role);

    @Mapping(target = "permissions", ignore = true)
    void updateRole(RoleRequest request, @MappingTarget Role role);
}
