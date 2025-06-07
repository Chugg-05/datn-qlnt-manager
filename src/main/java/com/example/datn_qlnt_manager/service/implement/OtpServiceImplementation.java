package com.example.datn_qlnt_manager.service.implement;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.Objects;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.example.datn_qlnt_manager.configuration.OtpProperties;
import com.example.datn_qlnt_manager.service.OtpService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OtpServiceImplementation implements OtpService {
    RedisTemplate<String, String> redisTemplate;
    OtpProperties otpProperties;

    static SecureRandom random = new SecureRandom();
    static String OTP_PREFIX = "otp:";

    @Override
    public String generateOtp(String email) {
        String otp = generateOtpCode();
        String key = buildKey(email); // Xây dựng key lưu trong Redis, tránh bị trùng

        redisTemplate.opsForValue().set(key, otp, getOtpExpiration()); // Lưu OTP vào Redis

        return otp;
    }

    @Override
    public boolean verifyOtp(String email, String otpInput) {
        String key = buildKey(email); // Lấy key tương ứng từ email
        String otpData = redisTemplate.opsForValue().get(key); // lấy otp trong redis theo key

        return Objects.equals(otpData, otpInput); // so sánh OTP nhập vào
    }

    @Override
    public void clearOtp(String email) {
        redisTemplate.delete(buildKey(email)); // Phương thức xóa OTP trong redis khi đã dùng hoặc là hết hạn
    }

    @Override
    public boolean isOtpExist(String email) {
        return redisTemplate.hasKey(buildKey(email)); // hasKey: kiểm tra redis có đang chứa key không
    }

    // Phương thức hỗ trợ tạo key lưu trữ OTP dựa trên email, chuẩn hóa key.
    private String buildKey(String email) { // tạo key dùng làm khóa lưu trữ OTP trong redis
        return OTP_PREFIX + email.toLowerCase();
    }

    private Duration getOtpExpiration() { // lấy ra đối tượng Duration với thời gian sống của OTP
        return Duration.ofMinutes(otpProperties.getExpiration());
    }

    private String generateOtpCode() { // random số ngẫu nhiên từ 0 -> 999999
        return String.format(
                "%06d",
                random.nextInt(1_000_000)); // "%06d" chuỗi 6 chữ số, nếu ít hơn 6 số tự động thêm 0 và đầu cho đủ 6 số
    }
}
