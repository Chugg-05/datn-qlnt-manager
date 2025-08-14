package com.example.datn_qlnt_manager.dto.response.tenant;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

import com.example.datn_qlnt_manager.common.Gender;
import com.example.datn_qlnt_manager.common.TenantStatus;

import com.example.datn_qlnt_manager.dto.response.contract.ContractResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TenantResponse implements Serializable {
    String id;
    String customerCode;
    String fullName;
    Gender gender;
    LocalDate dob;
    String email;
    String phoneNumber;
    String pictureUrl;
    TenantStatus tenantStatus;
    List<ContractResponse> contracts;
}
