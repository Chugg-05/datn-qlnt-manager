package com.example.datn_qlnt_manager.configuration;

import java.io.IOException;
import java.text.ParseException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.datn_qlnt_manager.entity.User;
import com.example.datn_qlnt_manager.exception.AppException;
import com.example.datn_qlnt_manager.exception.ErrorCode;
import com.example.datn_qlnt_manager.service.implement.CustomUserDetailService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
// Lớp này chịu trách nhiệm xác thực mã thông báo JWT trong các yêu cầu HTTP đến.
public class JwtTokenValidator extends OncePerRequestFilter {
    TokenProvider tokenProvider;
    CustomUserDetailService customUserDetailService;

    // Phương thức này được gọi một lần cho mỗi yêu cầu HTTP để xác thực mã thông báo JWT.
    @Override
    protected void doFilterInternal(
            HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        // Lấy tiêu đề Authorization từ yêu cầu HTTP
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        // Kiểm tra xem tiêu đề có hợp lệ và bắt đầu bằng "Bearer " hay không.
        if (header == null || !header.startsWith("Bearer ")) {
            // Nếu không có tiêu đề Authorization hợp lệ, tiếp tục chuỗi bộ lọc mà không xác thực.
            filterChain.doFilter(request, response);
            return;
        }

        header = header.substring(7); // Loại bỏ "Bearer " khỏi tiêu đề để lấy mã thông báo JWT thực tế.

        String email;
        try {
            // Xác thực mã thông báo JWT và trích xuất email người dùng từ nó.
            email = tokenProvider.verifyAndExtractEmail(header);
        } catch (ParseException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); //
            response.setContentType("application/json");
            response.getWriter().write("{\"code\":401,\"message\":\"Unauthenticated: Invalid or expired JWT token.\"}");
            return;
        }

        // Nếu email là null, ném ra ngoại lệ
        if (email == null) {
            throw new AppException(ErrorCode.INVALID_TOKEN);
        }

        // Tải người dùng từ dịch vụ chi tiết người dùng tùy chỉnh dựa trên email đã trích xuất.
        User user = customUserDetailService.loadUserByUsername(email);

        // Nếu người dùng không tồn tại, ném ra ngoại lệ
        var usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        // Thiết lập thông tin xác thực vào SecurityContextHolder để người dùng được xác thực.
        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
        // Tiếp tục chuỗi bộ lọc để xử lý yêu cầu tiếp theo.
        filterChain.doFilter(request, response);
    }
}
