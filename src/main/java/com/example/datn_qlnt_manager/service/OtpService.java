package com.example.datn_qlnt_manager.service;

public interface OtpService {
    String generateOtp(String email);

    boolean verifyOtp(String email, String otpInput);

    void clearOtp(String email);

    boolean isOtpExist(String email);
}
