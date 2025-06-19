package com.example.datn_qlnt_manager.configuration;

import java.security.interfaces.RSAPublicKey;
import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

import com.example.datn_qlnt_manager.service.implement.GoogleJwkCacheService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.datn_qlnt_manager.entity.User;
import com.example.datn_qlnt_manager.exception.AppException;
import com.example.datn_qlnt_manager.exception.ErrorCode;
import com.example.datn_qlnt_manager.service.RedisService;
import com.example.datn_qlnt_manager.utils.JwtUtil;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import io.micrometer.common.util.StringUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TokenProvider {
    static final String EMAIL_CLAIM = "email";
    static final String ISSUER = "TroHub88";
    final JwtUtil jwtUtil;
    final RedisService redisService;
    final GoogleJwkCacheService jwkCacheService;

    @Value("${google.client.id}")
    protected String CLIENT_ID;

    private static final String EXPECTED_ISSUER = "https://accounts.google.com";

    // Tạo token truy cập (access token)
    public String generateAccessToken(User user) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS256); // sử dụng thuật toán HS256 để ký token

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder() // tạo một bộ claims cho token
                .subject(user.getId()) // id của người dùng
                .issuer(ISSUER) // tên hệ thống
                .issueTime(Date.from(Instant.now())) // thời gian phát hành token
                .expirationTime(Date.from(
                        Instant.now().plus(jwtUtil.getValidDuration(), ChronoUnit.SECONDS))) // thời gian hết hạn
                .claim(EMAIL_CLAIM, user.getEmail()) // thêm claim email vào token
                .jwtID(UUID.randomUUID().toString()) // tạo một unique ID cho token (jti)
                .build();

        Payload payload = new Payload(
                jwtClaimsSet.toJSONObject()); // chuyển đổi bộ claims thành JSON và tạo payload
        JWSObject jwsObject = new JWSObject(header, payload); // tạo một JWSObject với header và payload đã tạo

        try {
            jwsObject.sign(new MACSigner(jwtUtil.getSecretKey())); // ký JWSObject bằng secret key
            return jwsObject.serialize(); // chuyển đổi JWSObject thành chuỗi và trả về
        } catch (JOSEException e) {
            throw new IllegalArgumentException(e);
        }
    }

    // Tạo token làm mới (refresh token)
    public String generateRefreshToken(User user) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS256);

        var claimSet = new JWTClaimsSet.Builder()
                .subject(user.getId())
                .issuer(ISSUER)
                .issueTime(Date.from(Instant.now()))
                .expirationTime(new Date(Instant.now()
                        .plus(jwtUtil.getRefreshableDuration(), ChronoUnit.SECONDS)
                        .toEpochMilli()))
                .claim(EMAIL_CLAIM, user.getEmail())
                .jwtID(UUID.randomUUID().toString())
                .build(); // Tạo bộ claims cho refresh token

        var payload = new Payload(claimSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(header, payload);

        try {
            jwsObject.sign(new MACSigner(jwtUtil.getSecretKey()));
        } catch (JOSEException e) {
            throw new IllegalArgumentException(e);
        }

        return jwsObject.serialize();
    }

    // Xác thực và giải mã token
    public SignedJWT verifyToken(String token) {
        if (StringUtils.isBlank(token)) {
            throw new AppException(ErrorCode.INVALID_TOKEN_FORMAT);
        }

        if (token.startsWith("Bearer")) {
            token = token.replace("Bearer", "").trim();
        }

        SignedJWT signedJWT;
        try {
            signedJWT = SignedJWT.parse(token); // Giải mã token thành SignedJWT
        } catch (ParseException e) {
            log.warn("Token parse failed: {}", e.getMessage());
            throw new AppException(ErrorCode.INVALID_TOKEN_FORMAT);
        }

        try {
            JWSVerifier verifier = new MACVerifier(jwtUtil.getSecretKey()); // Tạo verifier với secret key
            if (!signedJWT.verify(verifier)) { // Kiểm tra chữ ký của token
                log.warn("Token signature verification failed.");
                throw new AppException(ErrorCode.INVALID_SIGNATURE);
            }
        } catch (JOSEException e) {
            log.warn("JOSE exception during verification: {}", e.getMessage());
            throw new AppException(ErrorCode.INVALID_SIGNATURE);
        }

        try {
            Date expiration = signedJWT.getJWTClaimsSet().getExpirationTime(); // Lấy thời gian hết hạn từ claims
            if (expiration == null || expiration.before(Date.from(Instant.now()))) {
                log.warn("Token expired at: {}", expiration);
                throw new AppException(ErrorCode.EXPIRED_TOKEN);
            }

            String jti = signedJWT.getJWTClaimsSet().getJWTID(); // Lấy jti (unique ID) từ claims
            if (StringUtils.isNotBlank(redisService.get(jti))) { // Kiểm tra xem token có bị blacklist không
                log.warn("Token is blacklisted: jti={}", jti);
                throw new AppException(ErrorCode.TOKEN_BLACKLISTED);
            }

            return signedJWT; // Trả về SignedJWT đã xác thực
        } catch (ParseException e) {
            log.error("JWT claims parse failed: {}", e.getMessage(), e);
            throw new AppException(ErrorCode.UNAUTHORIZED);
        } catch (Exception e) {
            log.error("Unexpected error in verifyToken: {}", e.getMessage(), e);
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
    }

    // Lấy email từ token
    public String verifyAndExtractEmail(String token) throws ParseException {
        // Lấy claim email từ token
        Object emailClaim = this.verifyToken(token).getJWTClaimsSet().getClaim(EMAIL_CLAIM);

        if (Objects.isNull(emailClaim)) { // Kiểm tra xem claim email có tồn tại không
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        return emailClaim.toString(); // Trả về email từ claim
    }

    // Lấy id từ token
    public long verifyAndExtractTokenExpired(String token) throws ParseException {
        // Lấy claim thời gian hết hạn từ token
        Date expiredClaim = this.verifyToken(token).getJWTClaimsSet().getExpirationTime();

        if (Objects.isNull(expiredClaim)) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        return expiredClaim.getTime(); // Trả về thời gian hết hạn của token
    }

    // Xác minh ID Token của Google
    public Map<String, Object> verifyTokenIdGoogle(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token); // giải mã token thành SignedJWT
            String keyId = signedJWT.getHeader().getKeyID(); // lấy keyId từ header của token

            // Lấy JWK tương ứng với keyId
            JWK jwk = jwkCacheService.getJwkByKeyId(keyId)
                    .orElseThrow(() -> {
                        log.error("Không tìm thấy public key tương ứng: {}", keyId);
                        return new AppException(ErrorCode.UNAUTHORIZED);
                    });

            // ép kiểu JWK thành RSAPublicKey
            RSAPublicKey publicKey = jwk.toRSAKey().toRSAPublicKey();

            // Kiểm tra chữ ký của token bằng public key
            boolean isVerified = signedJWT.verify(new RSASSAVerifier(publicKey));
            if (!isVerified) {
                log.error("Token không hợp lệ (chữ ký sai).");
                throw new AppException(ErrorCode.INVALID_TOKEN);
            }

            // Kiểm tra các claims trong token
            Map<String, Object> claims = signedJWT.getJWTClaimsSet().getClaims();
            if (!EXPECTED_ISSUER.equals(claims.get("iss"))) {
                log.error("Sai issuer.");
                throw new AppException(ErrorCode.INVALID_ISSUER);
            }

            // Lấy claim "aud" từ token
            Object audClaim = claims.get("aud");
            if (!(audClaim instanceof List<?> audList) || audList.isEmpty()) {
                log.error("Audience không hợp lệ.");
                throw new AppException(ErrorCode.INVALID_AUDIENCE);
            }

            // Lấy giá trị đầu tiên trong danh sách audience
            String audience = String.valueOf(audList.getFirst());
            if (!CLIENT_ID.equals(audience)) {
                log.error("Sai audience.");
                throw new AppException(ErrorCode.INVALID_AUDIENCE);
            }

            // Lấy thời gian hết hạn từ claims
            long exp = signedJWT.getJWTClaimsSet().getExpirationTime().getTime();
            if (System.currentTimeMillis() > exp) {
                log.error("Token đã hết hạn.");
                throw new AppException(ErrorCode.EXPIRED_TOKEN);
            }

            // Trả về các claims đã xác thực
            return claims;
        } catch (AppException e) {
            log.error("Lỗi xác thực token: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Lỗi không xác định khi xác thực Google token: {}", e.getMessage(), e);
            throw new AppException(ErrorCode.INVALID_TOKEN);
        }
    }
}
