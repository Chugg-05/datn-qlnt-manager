package com.example.datn_qlnt_manager.configuration;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
// Class này cấu hình bảo mật cho ứng dụng Spring Boot, bao gồm xác thực JWT, CORS, và các endpoint công khai.
public class SecurityConfig {
    JwtTokenValidator jwtTokenValidator;
    UserDetailsService userDetailsService;
    private static final String[] PUBLIC_ENDPOINTS = {
        "/auth/login",
        "/auth/logout",
        "/auth/refresh-token",
        "/auth/register",
        "/auth/forgot-password",
        "/auth/verify-otp",
        "/auth/reset-password",
        "/emails/send",
        "/auth/login/oauth2/google/authentication/**",
        "/docs/**",
        "/swagger-ui.html",
        "/v3/api-docs/**",
        "/swagger-ui/**",
        "/swagger-resources/**",
        "/webjars/**",
        "/configuration/**",
        "/ws/**"
    };

    // Bean này cấu hình chuỗi bảo mật cho ứng dụng, bao gồm xác thực JWT, CORS, và các endpoint công khai.
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // Cấu hình CORS để cho phép các yêu cầu từ các nguồn khác nhau.
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(
                        auth -> auth.requestMatchers(PUBLIC_ENDPOINTS) // Các endpoint công khai không yêu cầu xác thực
                                .permitAll() // Cho phép truy cập không cần xác thực
                                .anyRequest() // Các endpoint còn lại yêu cầu xác thực
                                .authenticated())
                .addFilterBefore(
                        jwtTokenValidator,
                        UsernamePasswordAuthenticationFilter
                                .class) // Thêm bộ lọc xác thực JWT trước bộ lọc xác thực mặc định
                .exceptionHandling(exception -> exception.authenticationEntryPoint(
                        new JwtAuthenticationEntryPoint())) // Xử lý ngoại lệ xác thực JWT
                .csrf(AbstractHttpConfigurer::disable); // Vô hiệu hóa CSRF vì chúng ta sử dụng JWT cho xác thực

        // Trả về SecurityFilterChain đã cấu hình
        return http.build();
    }

    // Bean này cung cấp AuthenticationManager để xử lý xác thực người dùng.
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        // Lấy AuthenticationManager từ cấu hình xác thực
        return authenticationConfiguration.getAuthenticationManager();
    }

    // Bean này cấu hình JwtAuthenticationConverter để chuyển đổi JWT thành Authentication
    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();

        // Không thêm tiền tố vào quyền, vì sử dụng quyền từ JWT
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("");

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();

        // Thiết lập JwtGrantedAuthoritiesConverter để chuyển đổi các quyền từ JWT
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);

        // Thiết lập UserDetailsService để lấy thông tin người dùng từ JWT
        return jwtAuthenticationConverter;
    }

    // Bean này cấu hình DaoAuthenticationProvider để xác thực người dùng từ cơ sở dữ liệu
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        // Thiết lập UserDetailsService để lấy thông tin người dùng từ cơ sở dữ liệu
        provider.setUserDetailsService(userDetailsService);
        // Thiết lập PasswordEncoder để mã hóa mật khẩu
        provider.setPasswordEncoder(passwordEncoder());
        // Trả về DaoAuthenticationProvider đã cấu hình
        return provider;
    }

    // Bean này cấu hình CORS để cho phép các yêu cầu từ các nguồn khác nhau
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();

        // Thiết lập các nguồn được phép truy cập
        corsConfiguration.setAllowedOrigins(List.of("http://localhost:5173"));
        corsConfiguration.addAllowedMethod("*"); // Cho phép tất cả các phương thức HTTP (GET, POST, PUT, DELETE, v.v.)
        corsConfiguration.addAllowedHeader("*"); // Cho phép tất cả các header
        corsConfiguration.setExposedHeaders(List.of("Authorization")); // Cho phép header Authorization được lộ ra ngoài
        corsConfiguration.setAllowCredentials(true); // Cho phép gửi cookie và thông tin xác thực trong các yêu cầu CORS
        corsConfiguration.setMaxAge(3600L); // Thời gian cache CORS là 1 giờ (3600 giây)

        UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
        // Đăng ký cấu hình CORS cho tất cả các endpoint
        urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);

        return urlBasedCorsConfigurationSource;
    }

    // Bean này cung cấp PasswordEncoder để mã hóa mật khẩu người dùng
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }
}
