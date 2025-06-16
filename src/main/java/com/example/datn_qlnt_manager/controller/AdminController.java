package com.example.datn_qlnt_manager.controller;

import com.example.datn_qlnt_manager.dto.ApiResponse;
import com.example.datn_qlnt_manager.dto.request.UserUpdateForAdminRequest;
import com.example.datn_qlnt_manager.dto.response.UserResponse;
import com.example.datn_qlnt_manager.service.UserService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/admins")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AdminController {
    UserService userService;

    @GetMapping
    public ApiResponse<List<UserResponse>> getUsersForAdmin(@RequestParam("role") String role) {
        return ApiResponse.<List<UserResponse>>builder()
                .message("Get all users by role")
                .data(userService.getUsersForAdmin(role))
                .build();
    }

    @PatchMapping(value = "/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<UserResponse> updateUserForAdmin(
            @PathVariable("userId") String userId,
            @Valid @ModelAttribute UserUpdateForAdminRequest request,
            @RequestParam(name = "profilePictureFile", required = false) MultipartFile profilePictureFile
    ) {

        if (profilePictureFile != null && !profilePictureFile.isEmpty()) {
            request.setProfilePicture(userService.uploadProfilePicture(profilePictureFile));
        }

        return ApiResponse.<UserResponse>builder()
                .message("User updated!")
                .data(userService.updateUserForAdmin(userId, request))
                .build();
    }

    @DeleteMapping("/{userId}")
    public ApiResponse<String> deleteUser(@PathVariable("userId") String userId) {
        userService.deleteUser(userId);
        return ApiResponse.<String>builder().message("User has been deleted!").build();
    }

}
