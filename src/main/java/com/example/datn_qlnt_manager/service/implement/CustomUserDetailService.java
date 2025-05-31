package com.example.datn_qlnt_manager.service.implement;

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

    @Override // ghi đè phương thức từ lớp cha, dễ phát hiện lỗi khi viết sai tên phương thức
    @Transactional
    public User loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository
                .findWithRolesAndPermissionsByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_EMAIL_OR_PASSWORD));
    }
}
