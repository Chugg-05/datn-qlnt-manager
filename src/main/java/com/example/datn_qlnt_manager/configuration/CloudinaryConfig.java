package com.example.datn_qlnt_manager.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.cloudinary.Cloudinary;

@Configuration
public class CloudinaryConfig {
    // Cấu hình Cloudinary để tải hình ảnh lên
    @Value("${cloudinary-url}")
    private String cloudinaryUrl;

    // Lớp cấu hình này được sử dụng để thiết lập Cloudinary với URL được cung cấp.
    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(cloudinaryUrl);
    }
}
