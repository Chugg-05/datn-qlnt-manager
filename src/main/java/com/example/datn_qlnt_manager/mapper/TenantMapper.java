package com.example.datn_qlnt_manager.mapper;

import org.mapstruct.*;

import com.example.datn_qlnt_manager.dto.request.tenant.TenantCreationRequest;
import com.example.datn_qlnt_manager.dto.request.tenant.TenantUpdateRequest;
import com.example.datn_qlnt_manager.dto.response.tenant.TenantResponse;
import com.example.datn_qlnt_manager.entity.Tenant;

@Mapper(componentModel = "spring")
public interface TenantMapper {
    @Mapping(target = "customerCode", ignore = true)
    @Mapping(target = "tenantStatus", constant = "CHO_TAO_HOP_DONG")
    @Mapping(target = "hasAccount", constant = "false")
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "owner", ignore = true)
    Tenant toTenant(TenantCreationRequest request);

    @Mapping(target = "pictureUrl", source = "user.profilePicture")
    TenantResponse toTenantResponse(Tenant tenant);

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "customerCode", ignore = true)
    @Mapping(target = "tenantStatus", ignore = true)
    @Mapping(target = "hasAccount", ignore = true)
    @Mapping(target = "contractTenants", ignore = true)
    @Mapping(target = "email", ignore = true)
    void updateTenant(TenantUpdateRequest request, @MappingTarget Tenant tenant);
}
