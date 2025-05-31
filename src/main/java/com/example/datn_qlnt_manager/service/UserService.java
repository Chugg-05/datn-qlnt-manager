package com.example.datn_qlnt_manager.service;

import org.springframework.security.access.prepost.PreAuthorize;

import com.example.datn_qlnt_manager.dto.request.UserCreationRequest;
import com.example.datn_qlnt_manager.dto.request.UserUpdateRequest;
import com.example.datn_qlnt_manager.dto.response.UserDetailResponse;
import com.example.datn_qlnt_manager.entity.User;

public interface UserService {

    UserDetailResponse createUser(UserCreationRequest request);

    User findUserWithRolesAndPermissionsById(String id);

    User getCurrentUser();

    UserDetailResponse updateUser(String userId, UserUpdateRequest request);

    @PreAuthorize("hasRole('MANAGER')")
    void deleteUser(String userId);
}
