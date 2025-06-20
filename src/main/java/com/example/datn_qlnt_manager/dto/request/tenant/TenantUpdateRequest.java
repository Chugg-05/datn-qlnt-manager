package com.example.datn_qlnt_manager.dto.request.tenant;

import com.example.datn_qlnt_manager.common.Gender;
import com.example.datn_qlnt_manager.common.TenantStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TenantUpdateRequest implements Serializable {
        String fullName;
        Gender gender;
        Date dob;
        String identityCardNumber;
        String phoneNumber;
        String email;
        String address;
        TenantStatus tenantStatus;
        Boolean isRepresentative;
}