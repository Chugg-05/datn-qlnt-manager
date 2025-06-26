package com.example.datn_qlnt_manager.service.implement;

import java.text.ParseException;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.example.datn_qlnt_manager.common.Gender;
import com.example.datn_qlnt_manager.common.UserStatus;
import com.example.datn_qlnt_manager.configuration.TokenProvider;
import com.example.datn_qlnt_manager.dto.request.AuthenticationRequest;
import com.example.datn_qlnt_manager.dto.request.ResetPasswordRequest;
import com.example.datn_qlnt_manager.dto.response.LoginResponse;
import com.example.datn_qlnt_manager.dto.response.RefreshTokenResponse;
import com.example.datn_qlnt_manager.entity.User;
import com.example.datn_qlnt_manager.exception.AppException;
import com.example.datn_qlnt_manager.exception.ErrorCode;
import com.example.datn_qlnt_manager.repository.UserRepository;
import com.example.datn_qlnt_manager.repository.client.GoogleClient;
import com.example.datn_qlnt_manager.service.AuthenticationService;
import com.example.datn_qlnt_manager.service.OtpService;
import com.example.datn_qlnt_manager.service.RedisService;
import com.example.datn_qlnt_manager.utils.JwtUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.micrometer.common.util.StringUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationServiceImpl implements AuthenticationService {

    UserRepository userRepository;
    PasswordEncoder passwordEncoder;
    TokenProvider tokenProvider;
    AuthenticationManager authenticationManager;
    JwtUtil jwtUtil;
    RedisService redisService;
    OtpService otpService;
    GoogleClient googleClient;
    static String KEY_COOKIE = "TRO_HUB_SERVICE";
    static String GRANT_TYPE = "authorization_code";

    @NonFinal
    @Value("${google.client.id}")
    protected String CLIENT_ID;

    @NonFinal
    @Value("${google.client.secret}")
    protected String CLIENT_SECRET;

    @NonFinal
    @Value("${google.redirect.uri}")
    protected String REDIRECT_URI;

    // Method này sẽ được gọi khi người dùng đăng nhập bằng Google OAuth2
    @Override
    public LoginResponse authenticate(String code, HttpServletResponse response) {
        // Tạo một MultiValueMap để chứa các tham số cần thiết cho việc trao đổi mã thông báo
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();

        form.add("code", code); // Mã xác thực nhận được từ Google OAuth2
        form.add("client_id", CLIENT_ID); // ID của ứng dụng đã đăng ký với Google
        form.add("client_secret", CLIENT_SECRET); // Mật khẩu của ứng dụng đã đăng ký với Google
        form.add("redirect_uri", REDIRECT_URI); // URL chuyển hướng đã đăng ký với Google
        form.add("grant_type", GRANT_TYPE); // Loại yêu cầu trao đổi mã thông báo

        // Gọi API của Google để trao đổi mã xác thực lấy access token và id token
        var exchangeTokenResponse = googleClient.exchangeTokenResponse(form);

        // Kiểm tra xem có lỗi trong quá trình trao đổi mã xác thực không
        var claims = tokenProvider.verifyTokenIdGoogle(exchangeTokenResponse.getIdToken());

        // Trích xuất thông tin người dùng từ claims
        String email = claims.get("email").toString();
        String name = claims.get("name").toString();
        String profilePicture = claims.get("picture").toString();

        // Kiểm tra xem người dùng đã tồn tại trong hệ thống chưa
        if (!userRepository.existsByEmail(email)) {
            // Nếu người dùng chưa tồn tại, tạo mới người dùng
            User user = User.builder()
                    .email(email)
                    .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                    .fullName(name)
                    .gender(Gender.UNKNOWN)
                    .profilePicture(profilePicture)
                    .userStatus(UserStatus.ACTIVE)
                    .build();
            user.setCreatedAt(Instant.now());
            user.setUpdatedAt(Instant.now());

            return getLoginResponse(response, user);
        }
        // Nếu người dùng đã tồn tại, lấy thông tin người dùng từ cơ sở dữ liệu
        User user = userRepository.findByEmail(email).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        // Cập nhật thông tin người dùng nếu cần thiết
        return getLoginResponse(response, user); // Trả về thông tin đăng nhập đã được cập nhật
    }

    @Override
    public LoginResponse login(AuthenticationRequest request, HttpServletResponse response) {
        Authentication authentication;
        try {
            // Xác thực người dùng bằng email và mật khẩu
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        } catch (InternalAuthenticationServiceException | BadCredentialsException e) {
            throw new AppException(ErrorCode.INVALID_EMAIL_OR_PASSWORD);
        } catch (DisabledException ex) {
            throw new AppException(ErrorCode.USER_ALREADY_DELETED);
        } catch (LockedException ex) {
            throw new AppException(ErrorCode.USER_ALREADY_LOCKED);
        }

        // Lấy thông tin người dùng từ Authentication
        User user = (User) authentication.getPrincipal();

        return getLoginResponse(response, user);
    }

    @Override
    public void logout(String accessToken, HttpServletResponse response) throws ParseException {
        String email = tokenProvider.verifyAndExtractEmail(accessToken);
        User user = userRepository
                .findWithRolesAndPermissionsByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        long accessTokenExpired = tokenProvider.verifyAndExtractTokenExpired(accessToken);
        long currentTime = System.currentTimeMillis();

        // Nếu accessTokenExpired nhỏ hơn currentTime, token đã hết hạn
        if (currentTime < accessTokenExpired) {
            try {
                String jwtId =
                        tokenProvider.verifyToken(accessToken).getJWTClaimsSet().getJWTID(); // Lấy jwtId từ accessToken

                long ttl = accessTokenExpired - currentTime; // Tính toán thời gian còn lại của accessToken
                redisService.save(
                        jwtId, accessToken, ttl, TimeUnit.MILLISECONDS); // Lưu accessToken vào Redis với jwtId là key

                user.setRefreshToken(null);
                userRepository.save(user);

                deleteCookie(response); // Xóa cookie chứa accessToken và refreshToken

                SecurityContextHolder.clearContext(); // Xóa thông tin xác thực khỏi SecurityContext
            } catch (ParseException e) {
                throw new AppException(ErrorCode.INVALID_TOKEN);
            }
        }
    }

    @Override
    public void resetPassword(ResetPasswordRequest request) {
        User user = userRepository
                .findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.EMAIL_NOT_FOUND));

        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new AppException(ErrorCode.NEW_PASSWORD_SAME_AS_OLD);
        }

        if (!request.getNewPassword().equals(request.getReNewPassword())) {
            throw new AppException(ErrorCode.PASSWORDS_CONFIRM_NOT_MATCH);
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        otpService.clearOtp(request.getEmail());
    }

    @Override
    public RefreshTokenResponse refreshToken(String refreshToken, HttpServletResponse response) throws ParseException {
        if (StringUtils.isBlank(refreshToken)) {
            throw new AppException(ErrorCode.REFRESH_TOKEN_INVALID);
        }

        String email = tokenProvider.verifyAndExtractEmail(refreshToken);
        User user = userRepository
                .findWithRolesAndPermissionsByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        if (!Objects.equals(user.getRefreshToken(), refreshToken) || StringUtils.isBlank(user.getRefreshToken())) {
            throw new AppException(ErrorCode.REFRESH_TOKEN_INVALID);
        }

        var signJWT = tokenProvider.verifyToken(refreshToken);
        if (Objects.isNull(signJWT)) {
            throw new AppException(ErrorCode.REFRESH_TOKEN_INVALID);
        }

        String accessToken = tokenProvider.generateAccessToken(user);
        String generateRefreshToken = tokenProvider.generateRefreshToken(user);

        Cookie cookie = setCookie(accessToken, generateRefreshToken);
        response.addCookie(cookie);

        user.setRefreshToken(generateRefreshToken);
        userRepository.save(user);

        return RefreshTokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(generateRefreshToken)
                .build();
    }

    private Cookie setCookie(String accessToken, String refreshToken) {
        Map<String, String> tokenData = new HashMap<>();
        tokenData.put("accessToken", accessToken);
        tokenData.put("refreshToken", refreshToken);

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonData;

        try {
            jsonData = objectMapper.writeValueAsString(tokenData);
        } catch (JsonProcessingException | AppException e) {
            throw new AppException(ErrorCode.JSON_PROCESSING_ERROR);
        }

        // replace các kí tự sao cho giống với thư viện js-cookie
        // https://www.npmjs.com/package/js-cookie
        String formattedJsonData = jsonData.replace("\"", "%22").replace(",", "%2C");

        Cookie cookie = new Cookie(KEY_COOKIE, formattedJsonData);

        // cho phép lấy cookie từ phía client
        cookie.setHttpOnly(false);
        cookie.setMaxAge(jwtUtil.getRefreshableDuration().intValue()); // 2 weeks
        cookie.setPath("/");
        cookie.setSecure(true); // true nếu chỉ cho gửi qua HTTPS
        cookie.setDomain("localhost");
        return cookie;
    }

    @Override
    public void deleteCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie(KEY_COOKIE, "");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        cookie.setSecure(true);
        cookie.setDomain("localhost");
        response.addCookie(cookie);
    }

    private LoginResponse getLoginResponse(HttpServletResponse response, User user) {
        String accessToken = tokenProvider.generateAccessToken(user);
        String refreshToken = tokenProvider.generateRefreshToken(user);

        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        Cookie cookie = setCookie(accessToken, refreshToken);
        response.addCookie(cookie);

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .accessTokenTTL(jwtUtil.getValidDuration())
                .refreshTokenTTL(jwtUtil.getRefreshableDuration())
                .userId(user.getId())
                .build();
    }
}
