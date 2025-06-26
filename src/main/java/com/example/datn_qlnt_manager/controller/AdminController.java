package com.example.datn_qlnt_manager.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.datn_qlnt_manager.dto.ApiResponse;
import com.example.datn_qlnt_manager.dto.PaginatedResponse;
import com.example.datn_qlnt_manager.dto.filter.UserFilter;
import com.example.datn_qlnt_manager.dto.request.UserUpdateForAdminRequest;
import com.example.datn_qlnt_manager.dto.response.UserDetailResponse;
import com.example.datn_qlnt_manager.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/admins")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Admin", description = "API Admin")
public class AdminController {
    UserService userService;

    @Operation(summary = "Phân trang, tìm kiếm, lọc người dùng")
    @GetMapping
    public ApiResponse<List<UserDetailResponse>> filterUsers(
            @ModelAttribute UserFilter filter,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "15") int size) {

        PaginatedResponse<UserDetailResponse> result = userService.filterUsers(filter, page, size);

        return ApiResponse.<List<UserDetailResponse>>builder()
                .message("Filter users successfully")
                .data(result.getData())
                .meta(result.getMeta())
                .build();
    }

    @Operation(summary = "Lấy thông tin chi tiết người dùng")
    @GetMapping("/{userId}")
    public ApiResponse<UserDetailResponse> getUser(@PathVariable("userId") String userId) {
        return ApiResponse.<UserDetailResponse>builder()
                .message("User found!")
                .data(userService.getUserById(userId))
                .build();
    }

    @Operation(summary = "Cập nhật thông tin người dùng")
    @PatchMapping(value = "/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<UserDetailResponse> updateUserForAdmin(
            @PathVariable("userId") String userId,
            @Valid @ModelAttribute UserUpdateForAdminRequest request,
            @RequestParam(name = "profilePictureFile", required = false) MultipartFile profilePictureFile) {

        if (profilePictureFile != null && !profilePictureFile.isEmpty()) {
            request.setProfilePicture(userService.uploadProfilePicture(profilePictureFile));
        }

        return ApiResponse.<UserDetailResponse>builder()
                .message("User updated!")
                .data(userService.updateUserForAdmin(userId, request))
                .build();
    }

    @Operation(summary = "Khóa người dùng (chuyển trạng thái sang LOCKED)")
    @PutMapping("/lock/{userId}")
    public ApiResponse<String> lockUser(@PathVariable("userId") String userId) {
        userService.accountLockById(userId);
        return ApiResponse.<String>builder()
                .message("User has been locked")
                .data("User with ID " + userId + " has been locked.")
                .build();
    }

    @Operation(summary = "Khôi phục người dùng đã bị khóa")
    @PutMapping("/recover/{userId}")
    public ApiResponse<String> recoverUser(@PathVariable("userId") String userId) {
        userService.recoverLockedUsersById(userId);
        return ApiResponse.<String>builder()
                .message("User has been recovered")
                .data("User with ID " + userId + " has been recovered.")
                .build();
    }

    @Operation(summary = "Xóa người dùng")
    @DeleteMapping("/{userId}")
    public ApiResponse<String> deleteUser(@PathVariable("userId") String userId) {
        userService.deleteUserById(userId);
        return ApiResponse.<String>builder().message("User has been deleted!").build();
    }
}
