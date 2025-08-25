package com.example.datn_qlnt_manager.service.implement;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.example.datn_qlnt_manager.entity.*;
import com.example.datn_qlnt_manager.repository.ContractTenantRepository;
import com.example.datn_qlnt_manager.utils.FormatUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.datn_qlnt_manager.dto.request.EmailRequest;
import com.example.datn_qlnt_manager.dto.request.Recipient;
import com.example.datn_qlnt_manager.dto.request.SendEmailRequest;
import com.example.datn_qlnt_manager.dto.request.Sender;
import com.example.datn_qlnt_manager.dto.response.EmailResponse;
import com.example.datn_qlnt_manager.exception.AppException;
import com.example.datn_qlnt_manager.exception.ErrorCode;
import com.example.datn_qlnt_manager.repository.client.EmailClient;
import com.example.datn_qlnt_manager.service.EmailService;
import com.example.datn_qlnt_manager.utils.EmailTemplateUtil;

import feign.FeignException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EmailServiceImpl implements EmailService {
    EmailClient emailClient;
    ContractTenantRepository contractTenantRepository;

    @Value("${brevo.api.key}")
    @NonFinal
    String apiKey;

    @Value("${brevo.sender.name}")
    @NonFinal
    String name;

    @Value("${brevo.sender.email}")
    @NonFinal
    String email;

    @Override
    public EmailResponse sendEmail(SendEmailRequest request) {
        EmailRequest emailRequest = EmailRequest.builder()
                .sender(Sender.builder().name(name).email(email).build())
                .to(List.of(request.getTo()))
                .subject(request.getSubject())
                .htmlContent(request.getHtmlContent())
                .build();

        try {
            return emailClient.sendEmail(apiKey, emailRequest);
        } catch (FeignException exception) {
            log.error(
                    """
					--- FEIGN ERROR ---
					URL: {}
					Status: {}
					Message: {}
					Response Body: {}
					Headers: {}
					""",
                    exception.request().url(),
                    exception.status(),
                    exception.getMessage(),
                    exception.contentUTF8(),
                    exception.responseHeaders(),
                    exception);
            throw new AppException(ErrorCode.CANNOT_SEND_EMAIL);
        }
    }

    @Override
    public void sendAccountInfoToTenant(String recipientEmail, String recipientName, String rawPassword) {
        String subject = "Thông tin tài khoản khách thuê";

        String content = EmailTemplateUtil.loadTemplate(
                "tenant-account-info",
                Map.of(
                        "name", recipientName,
                        "email", recipientEmail,
                        "password", rawPassword));

        try {
            sendEmail(SendEmailRequest.builder()
                    .to(Recipient.builder()
                            .email(recipientEmail)
                            .name(recipientName)
                            .build())
                    .subject(subject)
                    .htmlContent(content)
                    .build());

            log.info("Tài khoản đã được gửi đến khách thuê: {}", recipientEmail);
        } catch (Exception e) {
            log.error("Gửi email tài khoản khách thuê thất bại: {}", recipientEmail, e);
            throw new AppException(ErrorCode.EMAIL_SENDING_FAILED);
        }
    }

    @Override
    public void sendOtpEmail(User user, String otp) {
        String subject = "Mã xác nhận đặt lại mật khẩu";
        String content =
                EmailTemplateUtil.loadTemplate("otp-forgot-password", Map.of("name", user.getFullName(), "otp", otp));

        try {
            sendEmail(SendEmailRequest.builder()
                    .to(Recipient.builder()
                            .name(user.getFullName())
                            .email(user.getEmail())
                            .build())
                    .subject(subject)
                    .htmlContent(content)
                    .build());
        } catch (Exception e) {
            log.error("Failed to send OTP email to: {}", user.getEmail(), e);
            throw new AppException(ErrorCode.EMAIL_SENDING_FAILED);
        }
    }

    @Override
    public void sendPaymentNotificationToTenant(
            String recipientEmail,
            String recipientName,
            Invoice invoice,
            PaymentReceipt receipt
    ) {
        String subject = String.format("Phiếu thanh toán tháng %d/%d đã được phát hành", invoice.getMonth(), invoice.getYear());

        String content = EmailTemplateUtil.loadTemplate(
                "tenant-payment-notification",
                Map.of(
                        "name", recipientName,
                        "invoiceCode", invoice.getInvoiceCode(),
                        "receiptCode", receipt.getReceiptCode(),
                        "amount", FormatUtil.formatCurrency(invoice.getTotalAmount()),
                        "dueDate", FormatUtil.formatDate(invoice.getPaymentDueDate()),
                        "building", invoice.getBuildingName(),
                        "room", invoice.getRoomCode(),
                        "invoiceType", FormatUtil.formatInvoiceType(invoice.getInvoiceType()),
                        "note", invoice.getNote() != null ? invoice.getNote() : "Không có"
                )
        );

        try {
            sendEmail(SendEmailRequest.builder()
                    .to(Recipient.builder()
                            .email(recipientEmail)
                            .name(recipientName)
                            .build())
                    .subject(subject)
                    .htmlContent(content)
                    .build());

            log.info("Đã gửi email thông báo hóa đơn đến: {}", recipientEmail);
        } catch (Exception e) {
            log.error("Gửi email thông báo hóa đơn thất bại: {}", recipientEmail, e);
            throw new AppException(ErrorCode.EMAIL_SENDING_FAILED);
        }
    }

    @Transactional
    @Override
    public void notifyOwnerForCashReceipt(PaymentReceipt receipt) {
        Invoice invoice = receipt.getInvoice();

        User owner = invoice.getContract().getRoom().getFloor().getBuilding().getUser();

        ContractTenant representative = contractTenantRepository
                .findByContractIdAndRepresentativeTrue(invoice.getContract().getId())
                .orElseThrow(() -> new AppException(ErrorCode.REPRESENTATIVE_NOT_FOUND));

        Tenant tenant = representative.getTenant();

        String subject = String.format("[THU TIỀN TRỰC TIẾP] Khách thuê đã chọn thanh toán tiền mặt - Hóa đơn %s", invoice.getInvoiceCode());

        String content = EmailTemplateUtil.loadTemplate(
                "notify-owner-cash-payment",
                Map.of(
                        "name", owner.getFullName(),
                        "invoiceCode", invoice.getInvoiceCode(),
                        "receiptCode", receipt.getReceiptCode(),
                        "amount", FormatUtil.formatCurrency(receipt.getAmount()),
                        "building", invoice.getBuildingName(),
                        "room", invoice.getRoomCode(),
                        "tenant", tenant.getFullName(),
                        "dueDate", FormatUtil.formatDate(invoice.getPaymentDueDate())
                ));

        try {
            sendEmail(SendEmailRequest.builder()
                    .to(Recipient.builder()
                            .email(owner.getEmail())
                            .name(owner.getFullName())
                            .build())
                    .subject(subject)
                    .htmlContent(content)
                    .build());

            log.info("Thông báo thanh toán tiền mặt đã gửi tới chủ nhà: {}", owner.getEmail());
        } catch (Exception e) {
            log.error("Gửi email thông báo chủ nhà thất bại: {}", owner.getEmail(), e);
            throw new AppException(ErrorCode.EMAIL_SENDING_FAILED);
        }

    }

    @Transactional
    @Override
    public void notifyOwnerRejectedReceipt(PaymentReceipt receipt) {
        Invoice invoice = receipt.getInvoice();
        User owner = invoice.getContract().getRoom().getFloor().getBuilding().getUser();

        ContractTenant representative = contractTenantRepository
                .findByContractIdAndRepresentativeTrue(invoice.getContract().getId())
                .orElseThrow(() -> new AppException(ErrorCode.REPRESENTATIVE_NOT_FOUND));

        Tenant tenant = representative.getTenant();

        String subject = String.format("[TỪ CHỐI THANH TOÁN] Khách thuê từ chối phiếu thanh toán - %s", receipt.getReceiptCode());

        String content = EmailTemplateUtil.loadTemplate(
                "notify-owner-rejected-payment",
                Map.of(
                        "name", owner.getFullName(),
                        "invoiceCode", invoice.getInvoiceCode(),
                        "receiptCode", receipt.getReceiptCode(),
                        "amount", FormatUtil.formatCurrency(receipt.getAmount()),
                        "building", invoice.getBuildingName(),
                        "room", invoice.getRoomCode(),
                        "tenant", tenant.getFullName(),
                        "dueDate", FormatUtil.formatDate(invoice.getPaymentDueDate()),
                        "reason", receipt.getNote()
                )
        );

        try {
            sendEmail(SendEmailRequest.builder()
                    .to(Recipient.builder()
                            .email(owner.getEmail())
                            .name(owner.getFullName())
                            .build())
                    .subject(subject)
                    .htmlContent(content)
                    .build());

            log.info("Email từ chối thanh toán đã được gửi tới chủ nhà: {}", owner.getEmail());
        } catch (Exception e) {
            log.error("Gửi email từ chối thanh toán thất bại: {}", owner.getEmail(), e);
            throw new AppException(ErrorCode.EMAIL_SENDING_FAILED);
        }
    }

    @Override
    public void notifyTenantPaymentConfirmed(PaymentReceipt receipt) {
        Invoice invoice = receipt.getInvoice();

        ContractTenant representative = contractTenantRepository
                .findByContractIdAndRepresentativeTrue(invoice.getContract().getId())
                .orElseThrow(() -> new AppException(ErrorCode.REPRESENTATIVE_NOT_FOUND));

        Tenant tenant = representative.getTenant();

        if (tenant == null || tenant.getUser() == null || tenant.getUser().getEmail() == null) {
            log.warn("Không thể gửi email vì không tìm thấy đại diện hợp lệ.");
            return;
        }

        String email = tenant.getUser().getEmail();
        String name = tenant.getFullName();

        String subject = String.format("Xác nhận thanh toán thành công - %s", receipt.getReceiptCode());

        String content = EmailTemplateUtil.loadTemplate(
                "payment-confirmed-to-tenant",
                Map.of(
                        "name", name,
                        "invoiceCode", invoice.getInvoiceCode(),
                        "receiptCode", receipt.getReceiptCode(),
                        "amount", FormatUtil.formatCurrency(receipt.getAmount()),
                        "building", invoice.getBuildingName(),
                        "room", invoice.getRoomCode(),
                        "paymentDate", FormatUtil.formatDateTime(receipt.getPaymentDate())
                )
        );

        sendEmail(SendEmailRequest.builder()
                .to(Recipient.builder()
                        .email(email)
                        .name(name)
                        .build())
                .subject(subject)
                .htmlContent(content)
                .build());
    }

    @Override
    public void notifyTenantDepositRefund(Deposit deposit) {
        Contract contract = deposit.getContract();

        ContractTenant representative = contractTenantRepository
                .findByContractIdAndRepresentativeTrue(contract.getId())
                .orElseThrow(() -> new AppException(ErrorCode.REPRESENTATIVE_NOT_FOUND));

        Tenant tenant = representative.getTenant();

        if (tenant == null || tenant.getUser() == null || tenant.getUser().getEmail() == null) {
            log.warn("Không tìm thấy đại diện hợp lệ.");
            return;
        }

        String email = tenant.getUser().getEmail();
        String name = tenant.getFullName();

        String subject = String.format("Xác nhận hoàn tiền cọc - Hợp đồng %s", contract.getContractCode());

        String content = EmailTemplateUtil.loadTemplate(
                "deposit-refund-to-tenant",
                Map.of(
                        "name", name,
                        "amount", FormatUtil.formatCurrency(deposit.getRefundAmount()),
                        "building", contract.getRoom().getFloor().getBuilding().getBuildingName(),
                        "room", contract.getRoom().getRoomCode(),
                        "refundDate", FormatUtil.formatDateTime(deposit.getDepositRefundDate()),
                        "note", deposit.getNote() != null ? deposit.getNote() : "Không có"
                )
        );

        sendEmail(SendEmailRequest.builder()
                .to(Recipient.builder().email(email).name(name).build())
                .subject(subject)
                .htmlContent(content)
                .build());
    }

    @Override
    public void notifyOwnerDepositReceived(Deposit deposit) {
        Contract contract = deposit.getContract();
        User owner = contract.getRoom().getFloor().getBuilding().getUser();

        ContractTenant representative = contractTenantRepository
                .findByContractIdAndRepresentativeTrue(contract.getId())
                .orElseThrow(() -> new AppException(ErrorCode.REPRESENTATIVE_NOT_FOUND));

        Tenant tenant = representative.getTenant();

        String subject = String.format("Khách thuê đã nhận lại tiền cọc - Hợp đồng %s", contract.getContractCode());

        String content = EmailTemplateUtil.loadTemplate(
                "deposit-received-to-owner",
                Map.of(
                        "ownerName", owner.getFullName(),
                        "tenantName", tenant.getFullName(),
                        "amount", FormatUtil.formatCurrency(deposit.getDepositAmount()),
                        "building", contract.getRoom().getFloor().getBuilding().getBuildingName(),
                        "room", contract.getRoom().getRoomCode(),
                        "receivedDate", FormatUtil.formatDateTime(deposit.getSecurityDepositReturnDate())
                )
        );

        sendEmail(SendEmailRequest.builder()
                .to(Recipient.builder().email(owner.getEmail()).name(owner.getFullName()).build())
                .subject(subject)
                .htmlContent(content)
                .build());
    }

    @Override
    public void notifyOwnerDepositNotReceived(Deposit deposit) {
        Contract contract = deposit.getContract();
        User owner = contract.getRoom().getFloor().getBuilding().getUser();

        ContractTenant representative = contractTenantRepository
                .findByContractIdAndRepresentativeTrue(contract.getId())
                .orElseThrow(() -> new AppException(ErrorCode.REPRESENTATIVE_NOT_FOUND));

        Tenant tenant = representative.getTenant();

        String subject = String.format("Khách thuê báo chưa nhận tiền cọc - Hợp đồng %s", contract.getContractCode());

        String content = EmailTemplateUtil.loadTemplate(
                "deposit-not-received-to-owner",
                Map.of(
                        "ownerName", owner.getFullName(),
                        "tenantName", tenant.getFullName(),
                        "amount", FormatUtil.formatCurrency(deposit.getDepositAmount()),
                        "building", contract.getRoom().getFloor().getBuilding().getBuildingName(),
                        "room", contract.getRoom().getRoomCode(),
                        "reportDate", FormatUtil.formatDateTime(LocalDateTime.now())
                )
        );

        sendEmail(SendEmailRequest.builder()
                .to(Recipient.builder().email(owner.getEmail()).name(owner.getFullName()).build())
                .subject(subject)
                .htmlContent(content)
                .build());
    }

}