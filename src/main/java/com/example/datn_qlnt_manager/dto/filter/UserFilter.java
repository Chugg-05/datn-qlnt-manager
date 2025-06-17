package com.example.datn_qlnt_manager.dto.filter;

import com.example.datn_qlnt_manager.common.Gender;
import com.example.datn_qlnt_manager.common.UserStatus;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserFilter {
    String fullName;
    String email;
    String phoneNumber;
    Gender gender;
    UserStatus userStatus;
    String role;
}
