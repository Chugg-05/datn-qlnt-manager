package com.example.datn_qlnt_manager.dto.response;

import com.example.datn_qlnt_manager.common.Gender;
import com.example.datn_qlnt_manager.common.UserStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDetailResponse implements Serializable {
    String fullName;
    Gender gender;
    LocalDate dob;
    String email;
    String phoneNumber;
    String profilePicture;
    UserStatus userStatus;
    Instant createdAt;
    Instant updatedAt;
    Set<RoleResponse> roles;
}
