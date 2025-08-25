package com.example.datn_qlnt_manager.service.implement;

import java.io.IOException;
import java.time.Instant;
import java.util.*;

import jakarta.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.example.datn_qlnt_manager.common.Meta;
import com.example.datn_qlnt_manager.common.Pagination;
import com.example.datn_qlnt_manager.common.UserStatus;
import com.example.datn_qlnt_manager.dto.PaginatedResponse;
import com.example.datn_qlnt_manager.dto.filter.UserFilter;
import com.example.datn_qlnt_manager.dto.request.UserCreationRequest;
import com.example.datn_qlnt_manager.dto.request.UserUpdateForAdminRequest;
import com.example.datn_qlnt_manager.dto.request.UserUpdateRequest;
import com.example.datn_qlnt_manager.dto.request.tenant.TenantCreationRequest;
import com.example.datn_qlnt_manager.dto.response.UserDetailResponse;
import com.example.datn_qlnt_manager.dto.response.UserResponse;
import com.example.datn_qlnt_manager.dto.statistics.UserStatistics;
import com.example.datn_qlnt_manager.entity.Role;
import com.example.datn_qlnt_manager.entity.User;
import com.example.datn_qlnt_manager.exception.AppException;
import com.example.datn_qlnt_manager.exception.ErrorCode;
import com.example.datn_qlnt_manager.mapper.UserMapper;
import com.example.datn_qlnt_manager.repository.RoleRepository;
import com.example.datn_qlnt_manager.repository.UserRepository;
import com.example.datn_qlnt_manager.service.EmailService;
import com.example.datn_qlnt_manager.service.UserService;
import com.example.datn_qlnt_manager.utils.PasswordUtil;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserServiceImpl implements UserService {
    UserRepository userRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;
    RoleRepository roleRepository;
    EmailService emailService;

    @Override
    public PaginatedResponse<UserDetailResponse> filterUsers(UserFilter filter, int page, int size) {
        // tạo đối tượng phân trang
        Pageable pageable = PageRequest.of(Math.max(0, page - 1), size); // đảm bảo page luôn >= 0

        // custom lọc người dùng theo dk kèm phân trang
        Page<User> paging = userRepository.filterUsersPaging(
                filter.getFullName(),
                filter.getEmail(),
                filter.getPhoneNumber(),
                filter.getGender(),
                filter.getUserStatus(),
                filter.getRole(),
                pageable);

        // chuyển ds user entity sang UserDetailResponse để return
        List<UserDetailResponse> users = paging.getContent().stream()
                .map(userMapper::toUserDetailResponse) // ánh xạ từng entity
                .toList();

        // tạo đối tượng chứa thông tin phân trang
        Meta<?> meta = Meta.builder()
                .pagination(Pagination.builder()
                        .count(paging.getNumberOfElements())
                        .perPage(size)
                        .currentPage(page)
                        .totalPages(paging.getTotalPages())
                        .total(paging.getTotalElements())
                        .build())
                .build();

        // trả về ds data và meta
        return PaginatedResponse.<UserDetailResponse>builder()
                .data(users)
                .meta(meta)
                .build();
    }

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

        return userMapper.toUserDetailResponse(userRepository.save(user));
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
    public UserResponse updateUser(String userId, UserUpdateRequest request) {
        User user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        if (!Objects.equals(user.getPhoneNumber(), request.getPhoneNumber())
                && userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new AppException(ErrorCode.PHONE_NUMBER_EXISTED);
        }

        userMapper.updateUser(request, user);
        user.setProfilePicture(request.getProfilePicture());
        user.setUpdatedAt(Instant.now());

        return userMapper.toUserResponse(userRepository.save(user));
    }

    @Override
    public UserDetailResponse updateUserForAdmin(String userId, UserUpdateForAdminRequest request) {
        User user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        if (!Objects.equals(user.getPhoneNumber(), request.getPhoneNumber())
                || userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new AppException(ErrorCode.PHONE_NUMBER_EXISTED);
        }

        userMapper.updateUserForAdmin(request, user);

        user.setProfilePicture(request.getProfilePicture());

        if (request.getRoles() != null && !request.getRoles().isEmpty()) {
            List<Role> roles = roleRepository.findAllById(request.getRoles());

            user.setRoles(new HashSet<>(roles));
        }

        user.setUpdatedAt(Instant.now());

        return userMapper.toUserDetailResponse(userRepository.save(user));
    }

    @Override
    public void deleteUserById(String userId) {
        if (!userRepository.existsById(userId)) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }
        userRepository.deleteById(userId);
    }

    @Override
    public void softDeleteUserById(String userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        if (user.getUserStatus() == UserStatus.DELETED) {
            throw new AppException(ErrorCode.USER_ALREADY_DELETED);
        }

        user.setUserStatus(UserStatus.DELETED);
        user.setUpdatedAt(Instant.now());
        // Cập nhật trạng thái người dùng thành DELETED
        userRepository.save(user);
    }

    @Override
    public void restoreUserById(String userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        if (user.getUserStatus() != UserStatus.DELETED) {
            throw new AppException(ErrorCode.USER_NOT_DELETED);
        }

        user.setUserStatus(UserStatus.ACTIVE);
        user.setUpdatedAt(Instant.now());
        // Cập nhật trạng thái người dùng thành ACTIVE
        userRepository.save(user);
    }

    @Override
    public void accountLockById(String userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        if (user.getUserStatus() == UserStatus.LOCKED) {
            throw new AppException(ErrorCode.USER_ALREADY_LOCKED);
        }

        user.setUserStatus(UserStatus.LOCKED);
        user.setUpdatedAt(Instant.now());
        // Cập nhật trạng thái người dùng thành LOCKED
        userRepository.save(user);
    }

    @Override
    public void recoverLockedUsersById(String userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        if (user.getUserStatus() != UserStatus.LOCKED) {
            throw new AppException(ErrorCode.USER_NOT_LOCKED);
        }

        user.setUserStatus(UserStatus.ACTIVE);
        user.setUpdatedAt(Instant.now());
        // Cập nhật trạng thái người dùng thành ACTIVE
        userRepository.save(user);
    }

    @Override
    public User createUserForTenant(TenantCreationRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_EXISTED);
        }

        if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new AppException(ErrorCode.PHONE_NUMBER_EXISTED);
        }

        String rawPassword = PasswordUtil.generateRandomPassword();
        String encodedPassword = passwordEncoder.encode(rawPassword);

        Role tenantRole =
                roleRepository.findByName("USER").orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .dob(request.getDob())
                .gender(request.getGender())
                .password(encodedPassword)
                .userStatus(UserStatus.ACTIVE)
                .roles(Set.of(tenantRole))
                .build();

        user.setCreatedAt(Instant.now());
        user.setCreatedAt(Instant.now());

        User savedUser = userRepository.save(user);

        emailService.sendAccountInfoToTenant(request.getEmail(), request.getFullName(), rawPassword);

        return savedUser;
    }

    @Override
    public User findUserWithRolesAndPermissionsById(String id) {
        return userRepository
                .findUserWithRolesAndPermissionsById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }

    @Override
    public UserDetailResponse getUserById(String userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        return userMapper.toUserDetailResponse(user);
    }

    @Override
    public UserStatistics totalUsersByStatus() {
        User user = getCurrentUser();
        return userRepository.getTotalUsersByStatus(user.getId());
    }

    @Override
    public User findById(String id) {
        return userRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }
}
