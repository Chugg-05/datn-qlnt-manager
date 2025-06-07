package com.example.datn_qlnt_manager.dto.request;

import com.example.datn_qlnt_manager.validator.constraints.EmailConstraints;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ForgotPasswordRequest {
    @EmailConstraints
    String email;
}
