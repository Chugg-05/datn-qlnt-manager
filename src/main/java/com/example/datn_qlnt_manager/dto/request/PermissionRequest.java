package com.example.datn_qlnt_manager.dto.request;

import java.io.Serializable;

import jakarta.validation.constraints.NotBlank;

import com.example.datn_qlnt_manager.validator.constraints.RoleNameAndPermissionConstraints;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PermissionRequest implements Serializable {
    @RoleNameAndPermissionConstraints
    String name;

    @NotBlank(message = "INVALID_DESCRIPTION_BLANK")
    String description;
}
