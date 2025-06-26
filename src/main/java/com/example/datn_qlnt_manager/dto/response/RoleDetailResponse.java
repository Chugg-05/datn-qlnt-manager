package com.example.datn_qlnt_manager.dto.response;

import java.io.Serializable;
import java.util.Set;

import lombok.*;
import lombok.experimental.FieldDefaults;

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
