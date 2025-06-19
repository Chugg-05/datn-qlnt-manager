package com.example.datn_qlnt_manager.service.implement;

import com.example.datn_qlnt_manager.common.UserStatus;
import jakarta.transaction.Transactional;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.datn_qlnt_manager.entity.User;
import com.example.datn_qlnt_manager.exception.AppException;
import com.example.datn_qlnt_manager.exception.ErrorCode;
import com.example.datn_qlnt_manager.repository.UserRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CustomUserDetailService implements UserDetailsService {
    UserRepository userRepository;

    // Phương pháp này được Spring Security sử dụng để tải thông tin chi tiết người dùng theo tên người dùng (trong trường hợp này là email).
    @Override
    @Transactional
    public User loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository
                .findWithRolesAndPermissionsByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_EMAIL_OR_PASSWORD));

        // Kiểm tra trạng thái của người dùng trước khi tải thông tin chi tiết
        if (user.getUserStatus() == UserStatus.DELETED) {
            throw new AppException(ErrorCode.USER_ALREADY_DELETED);
        }

        if (user.getUserStatus() == UserStatus.LOCKED) {
            throw new AppException(ErrorCode.USER_ALREADY_LOCKED);
        }

        return user;
    }
}
