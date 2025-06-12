package com.example.datn_qlnt_manager.service.implement;

import java.io.IOException;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.cloudinary.Cloudinary;
import jakarta.transaction.Transactional;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.datn_qlnt_manager.common.UserStatus;
import com.example.datn_qlnt_manager.dto.request.UserCreationRequest;
import com.example.datn_qlnt_manager.dto.request.UserUpdateRequest;
import com.example.datn_qlnt_manager.dto.response.UserDetailResponse;
import com.example.datn_qlnt_manager.entity.Role;
import com.example.datn_qlnt_manager.entity.User;
import com.example.datn_qlnt_manager.exception.AppException;
import com.example.datn_qlnt_manager.exception.ErrorCode;
import com.example.datn_qlnt_manager.mapper.UserMapper;
import com.example.datn_qlnt_manager.repository.RoleRepository;
import com.example.datn_qlnt_manager.repository.UserRepository;
import com.example.datn_qlnt_manager.service.UserService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserServiceImpl implements UserService {
    UserRepository userRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;
    RoleRepository roleRepository;
    Cloudinary cloudinary;

    @Override
    @Transactional
    public UserDetailResponse createUser(UserCreationRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_EXISTED);
        }

        if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new AppException(ErrorCode.PHONE_NUMBER_EXISTED);
        }

        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setUserStatus(UserStatus.ACTIVE);

        Role role = roleRepository.findByName("MANAGER").orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));

        user.setRoles(Set.of(role));
        user.setCreatedAt(Instant.now());
        user.setUpdatedAt(Instant.now());

        return userMapper.toUserResponse(userRepository.save(user));
    }

    @Override
    public User findUserWithRolesAndPermissionsById(String id) {
        return userRepository
                .findUserWithRolesAndPermissionsById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }

    @Override
    public User getCurrentUser() {
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            var user = (User) securityContext.getAuthentication().getPrincipal();
            return userRepository.findById(user.getId()).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        } catch (Exception e) {
            log.error("Cannot get current user: {}", e.getMessage());
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
    }

    @Override
    public UserDetailResponse updateUser(String userId, UserUpdateRequest request) {
        User user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        userMapper.updateUser(request, user);
        user.setProfilePicture(request.getProfilePicture());

        if (request.getRoles() != null && !request.getRoles().isEmpty()) {
            List<Role> roles = roleRepository.findAllById(request.getRoles());

            user.setRoles(new HashSet<>(roles));
        }

        user.setUpdatedAt(Instant.now());

        return userMapper.toUserResponse(userRepository.save(user));
    }

    @Override
    public void deleteUser(String userId) {
        userRepository.deleteById(userId);
    }

    @Override
    public String uploadProfilePicture(MultipartFile file) {
        try {
            Map<String, Object> uploadResult =
                    cloudinary.uploader().upload(file.getBytes(), Map.of("resource_type", "image"));
            return (String) uploadResult.get("secure_url");
        } catch (IOException | AppException e) {
            throw new AppException(ErrorCode.UPLOAD_FAILED);
        }
    }
}
