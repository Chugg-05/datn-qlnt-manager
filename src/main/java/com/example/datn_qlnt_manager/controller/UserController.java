package com.example.datn_qlnt_manager.controller;

import jakarta.validation.Valid;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.datn_qlnt_manager.dto.ApiResponse;
import com.example.datn_qlnt_manager.dto.request.UserUpdateRequest;
import com.example.datn_qlnt_manager.dto.response.UserResponse;
import com.example.datn_qlnt_manager.mapper.UserMapper;
import com.example.datn_qlnt_manager.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "User", description = "API User")
public class UserController {
    UserService userService;
    UserMapper userMapper;

    @Operation(summary = "Lấy thông tin người đang đăng nhập")
    @GetMapping("/me")
    public ApiResponse<UserResponse> getMyInfo() {
        var user = userService.getCurrentUser();

        return ApiResponse.<UserResponse>builder()
                .message("User found!")
                .data(userMapper.toUserResponse(user))
                .build();
    }

    @Operation(summary = "Cập nhật thông tin người dùng")
    @PatchMapping(value = "/me/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<UserResponse> updateUser(
            @Valid @ModelAttribute UserUpdateRequest request,
            @RequestParam(required = false) MultipartFile profilePictureFile) {
        var user = userService.getCurrentUser();

        if (profilePictureFile != null && !profilePictureFile.isEmpty()) {
            request.setProfilePicture(userService.uploadProfilePicture(profilePictureFile));
        }

        return ApiResponse.<UserResponse>builder()
                .message("User updated!")
                .data(userService.updateUser(user.getId(), request))
                .build();
    }
}
