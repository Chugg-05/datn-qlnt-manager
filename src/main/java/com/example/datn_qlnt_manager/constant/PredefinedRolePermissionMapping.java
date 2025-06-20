package com.example.datn_qlnt_manager.constant;

import java.util.List;

// Enum để ánh xạ vai trò với quyền
public enum PredefinedRolePermissionMapping {
    ADMIN(
            PredefinedRole.ADMIN_ROLE,
            List.of(
                    PredefinedPermission.READ,
                    PredefinedPermission.WRITE,
                    PredefinedPermission.EDIT,
                    PredefinedPermission.DELETE)),

    MANAGER(
            PredefinedRole.MANAGER_ROLE,
            List.of(PredefinedPermission.READ, PredefinedPermission.WRITE, PredefinedPermission.EDIT)),

    STAFF(PredefinedRole.STAFF_ROLE, List.of(PredefinedPermission.READ, PredefinedPermission.WRITE)),

    USER(PredefinedRole.USER_ROLE, List.of(PredefinedPermission.READ));

    public final String roleName;
    public final List<String> permissions;

    // Khởi tạo enum với tên vai trò và danh sách quyền
    PredefinedRolePermissionMapping(String roleName, List<String> permissions) {
        this.roleName = roleName;
        this.permissions = permissions;
    }

    // Trả về các quyền cho một tên vai trò nhất định
    public static List<String> getPermissionsForRole(String roleName) {
        for (PredefinedRolePermissionMapping entry : values()) {
            if (entry.roleName.equals(roleName)) return entry.permissions;
        }
        return List.of();
    }
}
