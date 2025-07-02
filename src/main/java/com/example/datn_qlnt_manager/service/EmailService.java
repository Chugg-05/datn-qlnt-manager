package com.example.datn_qlnt_manager.service;

import com.example.datn_qlnt_manager.dto.request.SendEmailRequest;
import com.example.datn_qlnt_manager.dto.response.EmailResponse;
import com.example.datn_qlnt_manager.entity.User;

public interface EmailService {
    EmailResponse sendEmail(SendEmailRequest request);

    void sendAccountInfoToTenant(String recipientEmail, String recipientName, String rawPassword);

    void sendOtpEmail(User user, String otp);
}
