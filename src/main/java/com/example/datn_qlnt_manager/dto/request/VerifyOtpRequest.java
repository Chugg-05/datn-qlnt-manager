package com.example.datn_qlnt_manager.dto.request;

import com.example.datn_qlnt_manager.validator.constraints.EmailConstraints;
import com.example.datn_qlnt_manager.validator.constraints.OtpConstraints;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VerifyOtpRequest {
    @EmailConstraints
    String email;

    @OtpConstraints
    String otpCode;
}
