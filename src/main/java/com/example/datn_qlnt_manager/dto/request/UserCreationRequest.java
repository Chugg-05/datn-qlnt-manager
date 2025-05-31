package com.example.datn_qlnt_manager.dto.request;

import java.io.Serializable;
import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import com.example.datn_qlnt_manager.common.Gender;
import com.example.datn_qlnt_manager.validator.constraints.DobConstraints;
import com.example.datn_qlnt_manager.validator.constraints.EmailConstraints;
import com.example.datn_qlnt_manager.validator.constraints.PasswordConstraints;
import com.example.datn_qlnt_manager.validator.constraints.PhoneNumberConstraints;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreationRequest implements Serializable {
    @EmailConstraints
    String email;

    @Size(min = 3, message = "INVALID_FULL_NAME")
    String fullName;

    @NotNull(message = "INVALID_GENDER_BLANK")
    Gender gender;

    @DobConstraints(min = 16, message = "INVALID_DOB")
    LocalDate dob;

    @PasswordConstraints
    String password;

    @PhoneNumberConstraints
    String phoneNumber;
}
