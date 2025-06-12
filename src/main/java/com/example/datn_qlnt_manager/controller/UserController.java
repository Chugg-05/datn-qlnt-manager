package com.example.datn_qlnt_manager.controller;

import com.example.datn_qlnt_manager.common.Gender;
import com.example.datn_qlnt_manager.common.UserStatus;
import jakarta.validation.Valid;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
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
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

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

    @PatchMapping(value = "/me/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<UserDetailResponse> updateUser(
            @RequestParam(required = false) String fullName,
            @RequestParam(required = false) Gender gender,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dob,
            @RequestParam(required = false) String phoneNumber,
            @RequestParam(required = false) UserStatus userStatus,
            @RequestParam(required = false) MultipartFile file
    ) {
        var user = userService.getCurrentUser();

        String uploadedImageUrl = null;
        if (file != null && !file.isEmpty()) {
            uploadedImageUrl = userService.uploadProfilePicture(file);
        }

        UserUpdateRequest request = UserUpdateRequest.builder()
                .fullName(fullName)
                .gender(gender)
                .dob(dob)
                .phoneNumber(phoneNumber)
                .userStatus(userStatus)
                .profilePicture(uploadedImageUrl)
                .build();

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
