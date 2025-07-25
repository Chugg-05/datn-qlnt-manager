package com.example.datn_qlnt_manager.dto.response.tenant;

import java.io.Serializable;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TenantBasicResponse implements Serializable {
    String id;
    String customerCode;
    String fullName;
    String email;
    String phoneNumber;
    Boolean isRepresentative;
}
