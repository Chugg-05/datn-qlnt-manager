package com.example.datn_qlnt_manager.dto.response.contractTenant;

import com.example.datn_qlnt_manager.common.Gender;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ContractTenantDetailResponse {
    String id;
    String contractId;
    String tenantId;
    String customerCode;
    String fullName;
    Gender gender;
    String phoneNumber;
    String email;
    Boolean representative;
    LocalDate startDate;
    LocalDate endDate;
    Instant createdAt;
    Instant updatedAt;
}
