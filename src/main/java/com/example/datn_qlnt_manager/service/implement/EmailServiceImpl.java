package com.example.datn_qlnt_manager.service.implement;

import java.util.List;
import java.util.Map;

import com.example.datn_qlnt_manager.entity.Invoice;
import com.example.datn_qlnt_manager.entity.PaymentReceipt;
import com.example.datn_qlnt_manager.utils.FormatUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.datn_qlnt_manager.dto.request.EmailRequest;
import com.example.datn_qlnt_manager.dto.request.Recipient;
import com.example.datn_qlnt_manager.dto.request.SendEmailRequest;
import com.example.datn_qlnt_manager.dto.request.Sender;
import com.example.datn_qlnt_manager.dto.response.EmailResponse;
import com.example.datn_qlnt_manager.entity.Invoice;
import com.example.datn_qlnt_manager.entity.PaymentReceipt;
import com.example.datn_qlnt_manager.entity.Tenant;
import com.example.datn_qlnt_manager.entity.User;
import com.example.datn_qlnt_manager.exception.AppException;
import com.example.datn_qlnt_manager.exception.ErrorCode;
import com.example.datn_qlnt_manager.repository.client.EmailClient;
import com.example.datn_qlnt_manager.service.EmailService;
import com.example.datn_qlnt_manager.utils.EmailTemplateUtil;
import com.example.datn_qlnt_manager.utils.FormatUtil;

import feign.FeignException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EmailServiceImpl implements EmailService {
    EmailClient emailClient;

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
            String recipientEmail, String recipientName, Invoice invoice, PaymentReceipt receipt) {
        String subject =
                String.format("Phiếu thanh toán tháng %d/%d đã được phát hành", invoice.getMonth(), invoice.getYear());

        String content = EmailTemplateUtil.loadTemplate(
                "tenant-payment-notification",
                Map.of(
                        "name", recipientName,
                        "invoiceCode", invoice.getInvoiceCode(),
                        "receiptCode", receipt.getReceiptCode(),
                        "amount", FormatUtil.formatCurrency(invoice.getTotalAmount()),
                        "dueDate", FormatUtil.formatDate(invoice.getPaymentDueDate()),
                        "building",
                        invoice.getContract()
                                .getRoom()
                                .getFloor()
                                .getBuilding()
                                .getBuildingName(),
                        "room", invoice.getContract().getRoom().getRoomCode(),
                        "invoiceType", FormatUtil.formatInvoiceType(invoice.getInvoiceType()),
                        "note", invoice.getNote() != null ? invoice.getNote() : "Không có"));

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
    public void notifyOwnerForCashReceipt(PaymentReceipt receipt, String representativeName) {
        Invoice invoice = receipt.getInvoice();
        String ownerEmail = invoice.getContract()
                .getRoom()
                .getFloor()
                .getBuilding()
                .getUser()
                .getEmail();
        String ownerName = invoice.getContract()
                .getRoom()
                .getFloor()
                .getBuilding()
                .getUser()
                .getFullName();

        String subject = String.format(
                "[THU TIỀN TRỰC TIẾP] Khách thuê đã chọn thanh toán tiền mặt - Hóa đơn %s", invoice.getInvoiceCode());

        String content = EmailTemplateUtil.loadTemplate(
                "notify-owner-cash-payment",
                Map.of(
                        "name", ownerName,
                        "invoiceCode", invoice.getInvoiceCode(),
                        "receiptCode", receipt.getReceiptCode(),
                        "amount", FormatUtil.formatCurrency(receipt.getAmount()),
                        "building",
                        invoice.getContract()
                                .getRoom()
                                .getFloor()
                                .getBuilding()
                                .getBuildingName(),
                        "room", invoice.getContract().getRoom().getRoomCode(),
                        "tenant", representativeName != null ? representativeName : "Không rõ",
                        "dueDate", FormatUtil.formatDate(invoice.getPaymentDueDate())));

        try {
            sendEmail(SendEmailRequest.builder()
                    .to(Recipient.builder().email(ownerEmail).name(ownerName).build())
                    .subject(subject)
                    .htmlContent(content)
                    .build());

            log.info("Thông báo thanh toán tiền mặt đã gửi tới chủ nhà: {}", ownerEmail);
        } catch (Exception e) {
            log.error("Gửi email thông báo chủ nhà thất bại: {}", ownerEmail, e);
            throw new AppException(ErrorCode.EMAIL_SENDING_FAILED);
        }
    }

    @Transactional
    @Override
    public void notifyOwnerRejectedReceipt(PaymentReceipt receipt, String representativeName) {
        Invoice invoice = receipt.getInvoice();
        String ownerEmail = invoice.getContract()
                .getRoom()
                .getFloor()
                .getBuilding()
                .getUser()
                .getEmail();
        String ownerName = invoice.getContract()
                .getRoom()
                .getFloor()
                .getBuilding()
                .getUser()
                .getFullName();

        String subject = String.format(
                "[TỪ CHỐI THANH TOÁN] Khách thuê từ chối phiếu thanh toán - %s", receipt.getReceiptCode());

        String content = EmailTemplateUtil.loadTemplate(
                "notify-owner-rejected-payment",
                Map.of(
                        "name", ownerName,
                        "invoiceCode", invoice.getInvoiceCode(),
                        "receiptCode", receipt.getReceiptCode(),
                        "amount", FormatUtil.formatCurrency(receipt.getAmount()),
                        "building",
                        invoice.getContract()
                                .getRoom()
                                .getFloor()
                                .getBuilding()
                                .getBuildingName(),
                        "room", invoice.getContract().getRoom().getRoomCode(),
                        "tenant", representativeName != null ? representativeName : "Không rõ",
                        "dueDate", FormatUtil.formatDate(invoice.getPaymentDueDate()),
                        "reason", receipt.getNote()));

        try {
            sendEmail(SendEmailRequest.builder()
                    .to(Recipient.builder().email(ownerEmail).name(ownerName).build())
                    .subject(subject)
                    .htmlContent(content)
                    .build());

            log.info("Email từ chối thanh toán đã được gửi tới chủ nhà: {}", ownerEmail);
        } catch (Exception e) {
            log.error("Gửi email từ chối thanh toán thất bại: {}", ownerEmail, e);
            throw new AppException(ErrorCode.EMAIL_SENDING_FAILED);
        }
    }

    @Override
    public void notifyTenantPaymentConfirmed(PaymentReceipt receipt) {
        Invoice invoice = receipt.getInvoice();
//        Tenant representative = invoice.getContract().getTenants().stream()
//                .filter(t -> Boolean.TRUE.equals(t.getHasAccount()))
//                .findFirst()
//                .orElse(null);

//        if (representative == null || representative.getUser() == null || representative.getUser().getEmail() == null) {
//            log.warn("Không thể gửi email vì không tìm thấy đại diện hợp lệ.");
//            return;
//        }
//
//        String tenantEmail = representative.getUser().getEmail();
//        String tenantName = representative.getFullName();
        if (representative == null
                || representative.getUser() == null
                || representative.getUser().getEmail() == null) {
            log.warn("Không thể gửi email vì không tìm thấy đại diện hợp lệ.");
            return;
        }

        String tenantEmail = representative.getUser().getEmail();
        String tenantName = representative.getFullName();

        String subject = String.format("Xác nhận thanh toán thành công - %s", receipt.getReceiptCode());

        String content = EmailTemplateUtil.loadTemplate(
                "payment-confirmed-to-tenant",
                Map.of(
//                        "name", tenantName,
                        "invoiceCode", invoice.getInvoiceCode(),
                        "receiptCode", receipt.getReceiptCode(),
                        "amount", FormatUtil.formatCurrency(receipt.getAmount()),
                        "building",
                        invoice.getContract()
                                .getRoom()
                                .getFloor()
                                .getBuilding()
                                .getBuildingName(),
                        "room", invoice.getContract().getRoom().getRoomCode(),
                        "paymentDate", FormatUtil.formatDateTime(receipt.getPaymentDate())));

        sendEmail(SendEmailRequest.builder()
                .to(Recipient.builder()
//                        .email(tenantEmail)
//                        .name(tenantName)
                        .build())
                .to(Recipient.builder().email(tenantEmail).name(tenantName).build())
                .subject(subject)
                .htmlContent(content)
                .build());
    }
}
