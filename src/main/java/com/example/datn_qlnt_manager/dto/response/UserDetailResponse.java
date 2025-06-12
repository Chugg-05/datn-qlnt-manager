package com.example.datn_qlnt_manager.dto.response;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Set;

import com.example.datn_qlnt_manager.common.Gender;
import com.example.datn_qlnt_manager.common.UserStatus;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDetailResponse implements Serializable {
    String id;
    String fullName;
    Gender gender;
    LocalDate dob;
    String email;
    String phoneNumber;
    String profilePicture;
    UserStatus userStatus;
    Instant createAt;
    Instant updateAt;
    Set<RoleResponse> roles;
}
