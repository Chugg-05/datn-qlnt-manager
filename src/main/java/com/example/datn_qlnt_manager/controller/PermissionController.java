package com.example.datn_qlnt_manager.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;

import com.example.datn_qlnt_manager.dto.ApiResponse;
import com.example.datn_qlnt_manager.dto.request.PermissionRequest;
import com.example.datn_qlnt_manager.dto.response.PermissionResponse;
import com.example.datn_qlnt_manager.service.PermissionService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/permissions")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PermissionController {
    PermissionService permissionService;

    @PostMapping
    public ApiResponse<PermissionResponse> createRole(@Valid @RequestBody PermissionRequest request) {
        return ApiResponse.<PermissionResponse>builder()
                .message("Permission has been created!")
                .data(permissionService.createPermission(request))
                .build();
    }

    @GetMapping
    public ApiResponse<List<PermissionResponse>> getRoles() {
        return ApiResponse.<List<PermissionResponse>>builder()
                .message("Permissions List")
                .data(permissionService.getPermissions())
                .build();
    }

    @DeleteMapping("/{permissionId}")
    public ApiResponse<String> deletePermission(@PathVariable("permissionId") String permissionId) {
        permissionService.deletePermission(permissionId);
        return ApiResponse.<String>builder()
                .data("Permission has been deleted!")
                .build();
    }

    @PutMapping("/{permissionId}")
    public ApiResponse<PermissionResponse> updatePermission(
            @Valid @RequestBody PermissionRequest request, @PathVariable("permissionId") String permissionId) {
        return ApiResponse.<PermissionResponse>builder()
                .message("Permission updated!")
                .data(permissionService.updatePermission(permissionId, request))
                .build();
    }
}
