package com.example.datn_qlnt_manager.configuration;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "otp")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OtpProperties {
    @Min(1)
    @Max(60) // chỉ cho phép giá trị từ 1 đến 60 phút
    long expiration;

    @Min(10)
    @Max(3600) // chỉ cho phép từ 10s đến 3600s
    long resend;
}
