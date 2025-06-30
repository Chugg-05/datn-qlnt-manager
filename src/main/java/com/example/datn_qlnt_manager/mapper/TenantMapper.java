package com.example.datn_qlnt_manager.mapper;

import org.mapstruct.*;

import com.example.datn_qlnt_manager.dto.request.tenant.TenantCreationRequest;
import com.example.datn_qlnt_manager.dto.request.tenant.TenantUpdateRequest;
import com.example.datn_qlnt_manager.dto.response.tenant.TenantDetailResponse;
import com.example.datn_qlnt_manager.dto.response.tenant.TenantResponse;
import com.example.datn_qlnt_manager.entity.Tenant;

import java.util.Set;

@Mapper(componentModel = "spring")
public interface TenantMapper {
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "customerCode", ignore = true)
    @Mapping(target = "tenantStatus", ignore = true)
    @Mapping(target = "isRepresentative", ignore = true)
    @Mapping(target = "hasAccount", ignore = true)
    Tenant toTenant(TenantCreationRequest request);

    TenantResponse toTenantResponse(Tenant tenant);

    @Mapping(source = "user.id", target = "userId")
    TenantDetailResponse toTenantDetailResponse(Tenant tenant);

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "customerCode", ignore = true)
    @Mapping(target = "tenantStatus", ignore = true)
    @Mapping(target = "isRepresentative", ignore = true)
    @Mapping(target = "hasAccount", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateTenant(TenantUpdateRequest request, @MappingTarget Tenant tenant);

    default TenantResponse getRepresentative(Set<TenantResponse> tenants) {
        return tenants.stream()
                .filter(TenantResponse::getIsRepresentative)
                .findFirst()
                .orElse(null);
    }
}
