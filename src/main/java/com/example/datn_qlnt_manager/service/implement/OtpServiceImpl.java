package com.example.datn_qlnt_manager.service.implement;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.Objects;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.example.datn_qlnt_manager.configuration.OtpProperties;
import com.example.datn_qlnt_manager.dto.request.Recipient;
import com.example.datn_qlnt_manager.dto.request.SendEmailRequest;
import com.example.datn_qlnt_manager.entity.User;
import com.example.datn_qlnt_manager.exception.AppException;
import com.example.datn_qlnt_manager.exception.ErrorCode;
import com.example.datn_qlnt_manager.repository.UserRepository;
import com.example.datn_qlnt_manager.service.EmailService;
import com.example.datn_qlnt_manager.service.OtpService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OtpServiceImpl implements OtpService {
    RedisTemplate<String, String> redisTemplate;
    OtpProperties otpProperties;
    UserRepository userRepository;
    EmailService emailService;

    static SecureRandom random = new SecureRandom();
    static String OTP_PREFIX = "otp:";

    @Override
    public void sendOtp(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new AppException(ErrorCode.EMAIL_NOT_FOUND));

        if (isOtpExist(email)) { // kiểm tra xem OTP đã được gửi chưa
            throw new AppException(ErrorCode.OTP_ALREADY_SENT);
        }

        String otp = generateOtp(email); // tạo random OTP

        sendOtpEmail(user, otp);

        log.info("OTP sent successfully to email: {}", email);
    }

    @Override
    public void verifyOtp(String email, String otpCode) {
        boolean isValid = verify(email, otpCode); // Kiểm tra OTP có hợp lệ không
        if (!isValid) {
            log.warn("Invalid OTP attempt for email: {}", email);
            throw new AppException(ErrorCode.INVALID_OTP_CODE);
        }
        log.info("OTP verified successfully for email: {}", email);
    }

    @Override
    public void clearOtp(String email) {
        redisTemplate.delete(buildKey(email)); // Phương thức xóa OTP trong redis khi đã dùng hoặc là hết hạn
    }

    private boolean isOtpExist(String email) {
        return redisTemplate.hasKey(buildKey(email)); // hasKey: kiểm tra redis có đang chứa key không
    }

    private String generateOtp(String email) {
        String otp = generateOtpCode();
        String key = buildKey(email); // Xây dựng key lưu trong Redis, tránh bị trùng

        redisTemplate.opsForValue().set(key, otp, getOtpExpiration()); // Lưu OTP vào Redis

        return otp;
    }

    private boolean verify(String email, String otpInput) {
        String key = buildKey(email); // Lấy key tương ứng từ email
        String otpData = redisTemplate.opsForValue().get(key); // lấy otp trong redis theo key

        return Objects.equals(otpData, otpInput); // so sánh OTP nhập vào
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

    private void sendOtpEmail(User user, String otp) {
        String subject = "Mã xác nhận đặt lại mật khẩu";
        String content = "<p>Xin chào " + user.getFullName() + "</p>"
                + "<p>Bạn đã yêu cầu đặt lại mật khẩu cho tài khoản TroHub của bạn.</p>"
                + "<p>Mã OTP của bạn là: <b>" + otp + "</b></p>"
                + "<p>Mã này có hiệu lực trong 5 phút.</p>"
                + "<p>Nếu không phải bạn thực hiện, không chia sẻ cho bất cứ ai mã OTP và vui lòng bỏ qua email này.</p>";

        try {
            emailService.sendEmail(SendEmailRequest.builder()
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
}
