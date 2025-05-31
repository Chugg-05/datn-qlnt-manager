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
public class RoleResponse implements Serializable {
    String id;
    String name;
    String description;
    Set<PermissionResponse> permissions;
}
