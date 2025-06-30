package com.example.datn_qlnt_manager.dto.response.tenant;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TenantBasicResponse implements Serializable {
    String customerCode;
    String fullName;
    String email;
    String phoneNumber;
    Boolean isRepresentative;
}
