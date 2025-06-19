package com.example.datn_qlnt_manager.configuration;

import java.io.IOException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import com.example.datn_qlnt_manager.dto.ApiResponse;
import com.example.datn_qlnt_manager.exception.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;

// Lớp này xử lý các nỗ lực truy cập trái phép bằng cách trả về phản hồi JSON có mã lỗi và thông báo.
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    // Phương thức này được gọi khi người dùng cố gắng truy cập một tài nguyên mà họ không có quyền truy cập.
    @Override
    public void commence(
            HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException {
        ErrorCode errorCode = ErrorCode.UNAUTHORIZED; // Mã lỗi mặc định cho truy cập trái phép

        response.setStatus(errorCode.getStatus().value()); // Thiết lập mã trạng thái HTTP cho phản hồi
        response.setContentType(MediaType.APPLICATION_JSON_VALUE); // Thiết lập loại nội dung của phản hồi là JSON

        // Tạo đối tượng ApiResponse với mã lỗi và thông báo từ ErrorCode
        ApiResponse<?> apiResponse = ApiResponse.builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .build();

        // Tạo một ObjectMapper để chuyển đổi đối tượng thành JSON
        ObjectMapper objectMapper = new ObjectMapper();

        // Ghi đối tượng ApiResponse vào phản hồi HTTP dưới dạng JSON
        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
        // Đảm bảo rằng tất cả dữ liệu đã được ghi vào phản hồi
        response.flushBuffer();
    }
}
