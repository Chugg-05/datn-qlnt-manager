package com.example.datn_qlnt_manager.dto.response.tenant;

import com.example.datn_qlnt_manager.common.Gender;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TenantLittleResponse implements Serializable {
    String id;
    String tenantId;
    String customerCode;
    String fullName;
    Gender gender;
    String phoneNumber;
    String email;
    Boolean representative;
}
