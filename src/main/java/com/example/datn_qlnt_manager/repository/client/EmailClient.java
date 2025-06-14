package com.example.datn_qlnt_manager.repository.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import com.example.datn_qlnt_manager.dto.request.EmailRequest;
import com.example.datn_qlnt_manager.dto.response.EmailResponse;

@FeignClient(
        name = "email-client",
        url = "https://api.brevo.com") // name: định danh Bean Spring Context, url: base url của brevo
public interface EmailClient {
    @PostMapping(
            value = "/v3/smtp/email", // đường dẫn api cụ thể kết hợp với url
            produces = MediaType.APPLICATION_JSON_VALUE // luôn dùng application_json cho Brevo
            )
    EmailResponse sendEmail(
            @RequestHeader("api-key") String apiKey, // truyền key api qua header
            @RequestBody EmailRequest request // thông tin (người nhận, nội dung..)
            );
}
