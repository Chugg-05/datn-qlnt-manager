package com.example.datn_qlnt_manager.dto.request;

import com.example.datn_qlnt_manager.validator.constraints.PasswordConstraints;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChangePasswordRequest {
    String oldPassword;

    @PasswordConstraints
    String newPassword;

    String reNewPassword;
}
