package com.example.datn_qlnt_manager.dto.request.ContractTenant;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ContractTenantCreationRequest {

    @NotBlank(message = "TENANT_NOT_FOUND")
    String tenantId;

    Boolean representative;
}
