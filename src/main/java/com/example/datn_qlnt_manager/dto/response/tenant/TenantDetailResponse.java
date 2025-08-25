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
    String customerCode;
    String fullName;
    Gender gender;
    LocalDate dob;
    String email;
    String phoneNumber;
    String pictureUrl;
    String identityCardNumber;
    String address;
    TenantStatus tenantStatus;
    Long totalContract;
    String frontCCCD;
    String backCCCD;
    Instant createdAt;
    Instant updatedAt;
}
