package com.example.datn_qlnt_manager.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoleDetailResponse implements Serializable {
    String name;
    String description;
    Set<PermissionDetailResponse> permissions;
}
