package com.example.datn_qlnt_manager.dto.request;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

import jakarta.validation.constraints.Size;

import com.example.datn_qlnt_manager.common.Gender;
import com.example.datn_qlnt_manager.common.UserStatus;
import com.example.datn_qlnt_manager.validator.constraints.DobConstraints;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UserUpdateRequest implements Serializable {
    @Size(min = 3, message = "INVALID_FULL_NAME")
    String fullName;

    Gender gender;

    @DobConstraints(min = 18, message = "INVALID_DOB")
    LocalDate dob;

    //    @PhoneNumberConstraints
    String phoneNumber;

    String profilePicture;

    UserStatus userStatus;

    List<String> roles;
}
