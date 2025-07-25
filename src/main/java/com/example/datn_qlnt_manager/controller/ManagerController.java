package com.example.datn_qlnt_manager.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.example.datn_qlnt_manager.dto.ApiResponse;
import com.example.datn_qlnt_manager.dto.PaginatedResponse;
import com.example.datn_qlnt_manager.dto.filter.UserFilter;
import com.example.datn_qlnt_manager.dto.response.UserDetailResponse;
import com.example.datn_qlnt_manager.dto.statistics.UserStatistics;
import com.example.datn_qlnt_manager.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/managers")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Manager", description = "API Manager")
public class ManagerController {
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
    @DeleteMapping("/{userId}")
    public ApiResponse<UserDetailResponse> getUser(@PathVariable("userId") String userId) {
        return ApiResponse.<UserDetailResponse>builder()
                .message("User found!")
                .data(userService.getUserById(userId))
                .build();
    }

    @Operation(summary = "Xóa người dùng (chuyển trang thái sang DELETED)")
    @PutMapping("/{userId}")
    public ApiResponse<String> softDeleteUser(@PathVariable("userId") String userId) {
        userService.softDeleteUserById(userId);
        return ApiResponse.<String>builder()
                .message("User has been deleted")
                .data("User with ID " + userId + " has been marked as deleted.")
                .build();
    }

    @Operation(summary = "Khôi phục người dùng đã bị xóa")
    @PutMapping("/restore/{userId}")
    public ApiResponse<String> restoreUser(@PathVariable("userId") String userId) {
        userService.restoreUserById(userId);
        return ApiResponse.<String>builder()
                .message("User has been restored")
                .data("User with ID " + userId + " has been restored.")
                .build();
    }

    @Operation(summary = "Thống kê người dùng theo trạng thái")
    @GetMapping("/statistics")
    public ApiResponse<UserStatistics> userStatistics() {
        return ApiResponse.<UserStatistics>builder()
                .message("Count users success!")
                .data(userService.totalUsersByStatus())
                .build();
    }
}
