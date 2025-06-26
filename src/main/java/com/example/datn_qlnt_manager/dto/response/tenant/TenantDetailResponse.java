package com.example.datn_qlnt_manager.dto.response.tenant;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;

import com.example.datn_qlnt_manager.common.Gender;
import com.example.datn_qlnt_manager.common.TenantStatus;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TenantDetailResponse implements Serializable {
    String id;
    String userId;
    String customerCode;
    String fullName;
    Gender gender;
    LocalDate dob;
    String identityCardNumber;
    String phoneNumber;
    String email;
    String address;
    TenantStatus tenantStatus;
    Boolean isRepresentative;
    Boolean hasAccount;
    Instant createdAt;
    Instant updatedAt;
}
