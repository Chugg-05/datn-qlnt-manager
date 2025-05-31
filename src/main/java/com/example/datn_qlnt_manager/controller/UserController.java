package com.example.datn_qlnt_manager.controller;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;

import com.example.datn_qlnt_manager.dto.ApiResponse;
import com.example.datn_qlnt_manager.dto.request.UserUpdateRequest;
import com.example.datn_qlnt_manager.dto.response.UserDetailResponse;
import com.example.datn_qlnt_manager.mapper.UserMapper;
import com.example.datn_qlnt_manager.service.UserService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {
    UserService userService;
    UserMapper userMapper;

    @GetMapping("/me")
    public ApiResponse<UserDetailResponse> getMyInfo() {
        var user = userService.getCurrentUser();

        return ApiResponse.<UserDetailResponse>builder()
                .message("User found!")
                .data(userMapper.toUserResponse(user))
                .build();
    }

    @PatchMapping("/me/update")
    public ApiResponse<UserDetailResponse> updateUser(@Valid @RequestBody UserUpdateRequest request) {
        var user = userService.getCurrentUser();

        return ApiResponse.<UserDetailResponse>builder()
                .message("User updated!")
                .data(userService.updateUser(user.getId(), request))
                .build();
    }

    @DeleteMapping("/delete/{userId}")
    public ApiResponse<String> deleteUser(@PathVariable("userId") String userId) {
        userService.deleteUser(userId);
        return ApiResponse.<String>builder().message("User has been deleted!").build();
    }
}
