package com.example.datn_qlnt_manager.service;

import com.example.datn_qlnt_manager.dto.response.UserDetailResponse;
import org.springframework.web.multipart.MultipartFile;

import com.example.datn_qlnt_manager.dto.PaginatedResponse;
import com.example.datn_qlnt_manager.dto.filter.UserFilter;
import com.example.datn_qlnt_manager.dto.request.UserCreationRequest;
import com.example.datn_qlnt_manager.dto.request.UserUpdateForAdminRequest;
import com.example.datn_qlnt_manager.dto.request.UserUpdateRequest;
import com.example.datn_qlnt_manager.dto.response.UserResponse;
import com.example.datn_qlnt_manager.entity.User;

public interface UserService {

    UserDetailResponse createUser(UserCreationRequest request);

    void softDeleteUserById(String userId);

    void restoreUserById(String userId);

    void accountLockById(String userId);

    void recoverLockedUsersById(String userId);

    User findUserWithRolesAndPermissionsById(String id);

    User getCurrentUser();

    UserResponse updateUser(String userId, UserUpdateRequest request);

    void deleteUserById(String userId);

    String uploadProfilePicture(MultipartFile file);

    UserDetailResponse getUserById(String userId);

    PaginatedResponse<UserDetailResponse> filterUsers(UserFilter filter, int page, int size);

    User findById(String id);

    UserDetailResponse updateUserForAdmin(String userId, UserUpdateForAdminRequest request);
}
