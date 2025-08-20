package com.example.datn_qlnt_manager.dto.response.contractTenant;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ContractTenantResponse {
    String id;
    String contractId;
    String tenantId;
    Boolean representative;
    LocalDate startDate;
    LocalDate endDate;
    Instant createdAt;
    Instant updatedAt;
}
