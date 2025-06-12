package com.example.datn_qlnt_manager.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;

import jakarta.servlet.http.HttpServletResponse;

import com.example.datn_qlnt_manager.dto.request.AuthenticationRequest;
import com.example.datn_qlnt_manager.dto.request.ResetPasswordRequest;
import com.example.datn_qlnt_manager.dto.response.LoginResponse;
import com.example.datn_qlnt_manager.dto.response.RefreshTokenResponse;
import com.nimbusds.jose.JOSEException;

public interface AuthenticationService {
    LoginResponse authenticate(String code, HttpServletResponse response)
            throws ParseException, IOException, JOSEException;

    LoginResponse login(AuthenticationRequest request, HttpServletResponse response)
            throws UnsupportedEncodingException;

    RefreshTokenResponse refreshToken(String refreshToken, HttpServletResponse response) throws ParseException;

    void logout(String token, HttpServletResponse response) throws ParseException;

    void deleteCookie(HttpServletResponse response);

    void resetPassword(ResetPasswordRequest request);
}
