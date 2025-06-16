package com.example.datn_qlnt_manager.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.Map;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import com.example.datn_qlnt_manager.common.Meta;
import com.example.datn_qlnt_manager.dto.ApiResponse;
import com.example.datn_qlnt_manager.dto.request.*;
import com.example.datn_qlnt_manager.dto.response.*;
import com.example.datn_qlnt_manager.entity.User;
import com.example.datn_qlnt_manager.mapper.UserMapper;
import com.example.datn_qlnt_manager.service.AuthenticationService;
import com.example.datn_qlnt_manager.service.OtpService;
import com.example.datn_qlnt_manager.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JOSEException;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {
    AuthenticationService authenticationService;
    UserService userService;
    UserMapper userMapper;
    OtpService otpService;

    @PostMapping("/register")
    public ApiResponse<UserResponse> register(@Valid @RequestBody UserCreationRequest request) {
        var user = userService.createUser(request);

        return ApiResponse.<UserResponse>builder()
                .message("User registered!")
                .data(user)
                .build();
    }

    @PostMapping("/login/oauth2/google/authentication")
    public ApiResponse<?> loginWithGoogle(@RequestParam("code") String code, HttpServletResponse response)
            throws ParseException, IOException, JOSEException {
        LoginResponse loginResponse = authenticationService.authenticate(code, response);

        return ApiResponse.builder()
                .message("Login with google successful!")
                .data(userMapper.toUserResponse(userService.findById(loginResponse.getUserId())))
                .meta(Meta.<LoginResponse>builder().tokenInfo(loginResponse).build())
                .build();
    }

    @PostMapping("/login")
    public ApiResponse<?> login(@RequestBody AuthenticationRequest request, HttpServletResponse response)
            throws UnsupportedEncodingException {
        LoginResponse loginResponse = authenticationService.login(request, response);

        User user = userService.findUserWithRolesAndPermissionsById(loginResponse.getUserId());
        UserResponse userResponse = userMapper.toUserResponse(user);

        loginResponse.setUserId(null);

        Meta<LoginResponse> meta =
                Meta.<LoginResponse>builder().tokenInfo(loginResponse).build();

        return ApiResponse.builder()
                .message("Login successful!")
                .meta(meta)
                .data(userResponse)
                .build();
    }

    @PostMapping("/refresh-token")
    public ApiResponse<?> refreshToken(
            @CookieValue(name = "TRO_HUB_SERVICE") String cookieValue, HttpServletResponse response)
            throws ParseException, JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> tokenData =
                objectMapper.readValue(cookieValue, new TypeReference<Map<String, String>>() {});

        String refreshToken = tokenData.get("refreshToken");

        var data = authenticationService.refreshToken(refreshToken, response);
        return ApiResponse.builder()
                .message("Refresh token successful!")
                .data(data)
                .build();
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token, HttpServletResponse response)
            throws ParseException {
        authenticationService.logout(token, response);

        return ApiResponse.<Void>builder().message("Logout successful!").build();
    }

    @PostMapping("/forgot-password")
    public ApiResponse<String> sendOtp(@Valid @RequestBody ForgotPasswordRequest request) {
        otpService.sendOtp(request.getEmail());

        return ApiResponse.<String>builder()
                .message("OTP code sent successfully!")
                .build();
    }

    @PostMapping("/verify-otp")
    public ApiResponse<String> verifyOtp(@Valid @RequestBody VerifyOtpRequest request) {
        otpService.verifyOtp(request.getEmail(), request.getOtpCode());

        return ApiResponse.<String>builder().message("Valid OTP Code").build();
    }

    @PostMapping("/reset-password")
    public ApiResponse<String> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authenticationService.resetPassword(request);

        return ApiResponse.<String>builder()
                .message("Password reset successful")
                .build();
    }
}
