package com.example.datn_qlnt_manager.service;

import org.springframework.transaction.annotation.Transactional;

import com.example.datn_qlnt_manager.dto.request.SendEmailRequest;
import com.example.datn_qlnt_manager.dto.response.EmailResponse;
import com.example.datn_qlnt_manager.entity.Deposit;
import com.example.datn_qlnt_manager.entity.Invoice;
import com.example.datn_qlnt_manager.entity.PaymentReceipt;
import com.example.datn_qlnt_manager.entity.User;

public interface EmailService {
    EmailResponse sendEmail(SendEmailRequest request);

    void sendAccountInfoToTenant(String recipientEmail, String recipientName, String rawPassword);

    void sendOtpEmail(User user, String otp);

    void sendPaymentNotificationToTenant(
            String recipientEmail, String recipientName, Invoice invoice, PaymentReceipt receipt);

    @Transactional
    void notifyOwnerForCashReceipt(PaymentReceipt receipt);

    @Transactional
    void notifyOwnerRejectedReceipt(PaymentReceipt receipt);

    void notifyTenantPaymentConfirmed(PaymentReceipt receipt);

    void notifyTenantDepositRefund(Deposit deposit);

    void notifyOwnerDepositReceived(Deposit deposit);

    void notifyOwnerDepositNotReceived(Deposit deposit);
}
