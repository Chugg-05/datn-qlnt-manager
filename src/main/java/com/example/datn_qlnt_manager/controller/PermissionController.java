package com.example.datn_qlnt_manager.controller;

import java.util.List;

import com.example.datn_qlnt_manager.dto.PaginatedResponse;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;

import com.example.datn_qlnt_manager.dto.ApiResponse;
import com.example.datn_qlnt_manager.dto.request.PermissionRequest;
import com.example.datn_qlnt_manager.dto.response.PermissionResponse;
import com.example.datn_qlnt_manager.service.PermissionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/permissions")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Permission", description = "API Permission")
public class PermissionController {
    PermissionService permissionService;

    @Operation(summary = "Phân trang, tìm kiếm, lọc quyền (admin)")
    @GetMapping
    public ApiResponse<List<PermissionResponse>> filterPermissions(
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "15") int size) {

        PaginatedResponse<PermissionResponse> result = permissionService.filterPermissions(name, page, size);

        return ApiResponse.<List<PermissionResponse>>builder()
                .message("Filter permissions successfully")
                .data(result.getData())
                .meta(result.getMeta())
                .build();
    }

    @Operation(summary = "Tạo quyền")
    @PostMapping
    public ApiResponse<PermissionResponse> createRole(@Valid @RequestBody PermissionRequest request) {
        return ApiResponse.<PermissionResponse>builder()
                .message("Permission has been created!")
                .data(permissionService.createPermission(request))
                .build();
    }

    @Operation(summary = "Xóa quyền")
    @DeleteMapping("/{permissionId}")
    public ApiResponse<String> deletePermission(@PathVariable("permissionId") String permissionId) {
        permissionService.deletePermission(permissionId);
        return ApiResponse.<String>builder()
                .data("Permission has been deleted!")
                .build();
    }

    @Operation(summary = "Cập nhật quyền")
    @PutMapping("/{permissionId}")
    public ApiResponse<PermissionResponse> updatePermission(
            @Valid @RequestBody PermissionRequest request, @PathVariable("permissionId") String permissionId) {
        return ApiResponse.<PermissionResponse>builder()
                .message("Permission updated!")
                .data(permissionService.updatePermission(permissionId, request))
                .build();
    }
}
