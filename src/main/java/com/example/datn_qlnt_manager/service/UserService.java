package com.example.datn_qlnt_manager.service;

import org.springframework.web.multipart.MultipartFile;

import com.example.datn_qlnt_manager.dto.request.UserCreationRequest;
import com.example.datn_qlnt_manager.dto.request.UserUpdateRequest;
import com.example.datn_qlnt_manager.dto.response.UserDetailResponse;
import com.example.datn_qlnt_manager.entity.User;

public interface UserService {

    UserDetailResponse createUser(UserCreationRequest request);

    User findUserWithRolesAndPermissionsById(String id);

    User getCurrentUser();

    UserDetailResponse updateUser(String userId, UserUpdateRequest request);

    void deleteUser(String userId);

    String uploadProfilePicture(MultipartFile file);

    User findById(String id);
}
