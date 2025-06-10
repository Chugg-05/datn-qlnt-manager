package com.example.datn_qlnt_manager.service;

public interface OtpService {
    void sendOtp(String email);


    void verifyOtp(String email, String otpCode);

    void clearOtp(String email);

}
