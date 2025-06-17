package com.example.datn_qlnt_manager.service;

import com.example.datn_qlnt_manager.dto.PaginatedResponse;
import com.example.datn_qlnt_manager.dto.filter.UserFilter;
import com.example.datn_qlnt_manager.dto.request.UserUpdateForAdminRequest;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;

import com.example.datn_qlnt_manager.dto.request.UserCreationRequest;
import com.example.datn_qlnt_manager.dto.request.UserUpdateRequest;
import com.example.datn_qlnt_manager.dto.response.UserResponse;
import com.example.datn_qlnt_manager.entity.User;

import java.util.List;

public interface UserService {

    UserResponse createUser(UserCreationRequest request);

    User findUserWithRolesAndPermissionsById(String id);

    User getCurrentUser();

    UserResponse updateUser(String userId, UserUpdateRequest request);

    void deleteUser(String userId);

    String uploadProfilePicture(MultipartFile file);

    UserResponse getUser(@PathVariable("userId") String userId);

    PaginatedResponse<UserResponse> filterUsers(UserFilter filter, int page, int size);

    User findById(String id);

    UserResponse updateUserForAdmin(String userId, UserUpdateForAdminRequest request);
}
