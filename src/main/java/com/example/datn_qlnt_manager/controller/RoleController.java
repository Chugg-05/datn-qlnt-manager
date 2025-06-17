package com.example.datn_qlnt_manager.controller;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;

import com.example.datn_qlnt_manager.dto.ApiResponse;
import com.example.datn_qlnt_manager.dto.request.RoleRequest;
import com.example.datn_qlnt_manager.dto.response.RoleResponse;
import com.example.datn_qlnt_manager.service.RoleService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/roles")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Role", description = "API Role")
public class RoleController {
    RoleService roleService;

    @Operation(summary = "Tạo vai trò")
    @PostMapping
    public ApiResponse<RoleResponse> createRole(@Valid @RequestBody RoleRequest request) {
        return ApiResponse.<RoleResponse>builder()
                .message("Role has been created!")
                .data(roleService.createRole(request))
                .build();
    }

    @Operation(summary = "Lấy danh sách role")
    @GetMapping
    public ApiResponse<List<RoleResponse>> getRoles() {
        return ApiResponse.<List<RoleResponse>>builder()
                .message("Role List")
                .data(roleService.getRoles())
                .build();
    }

    @Operation(summary = "Xóa vai trò")
    @DeleteMapping("/{roleId}")
    public ApiResponse<String> deleteRole(@PathVariable("roleId") String roleId) {
        roleService.deleteRole(roleId);
        return ApiResponse.<String>builder().data("Role has been deleted!").build();
    }

    @Operation(summary = "Cập nhật quyền")
    @PutMapping("/{roleId}")
    public ApiResponse<RoleResponse> updateRole(
            @Valid @RequestBody RoleRequest request, @PathVariable("roleId") String roleId) {
        return ApiResponse.<RoleResponse>builder()
                .message("Role updated!")
                .data(roleService.updateRole(roleId, request))
                .build();
    }
}
