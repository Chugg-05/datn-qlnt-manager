package com.example.datn_qlnt_manager.service.implement;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.datn_qlnt_manager.dto.request.EmailRequest;
import com.example.datn_qlnt_manager.dto.request.SendEmailRequest;
import com.example.datn_qlnt_manager.dto.request.Sender;
import com.example.datn_qlnt_manager.dto.response.EmailResponse;
import com.example.datn_qlnt_manager.exception.AppException;
import com.example.datn_qlnt_manager.exception.ErrorCode;
import com.example.datn_qlnt_manager.repository.client.EmailClient;
import com.example.datn_qlnt_manager.service.EmailService;

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
}
