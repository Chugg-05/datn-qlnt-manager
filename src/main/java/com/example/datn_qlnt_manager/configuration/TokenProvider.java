package com.example.datn_qlnt_manager.configuration;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

import org.springframework.http.HttpHeaders;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Service;

import com.example.datn_qlnt_manager.entity.User;
import com.example.datn_qlnt_manager.exception.AppException;
import com.example.datn_qlnt_manager.exception.ErrorCode;
import com.example.datn_qlnt_manager.service.RedisService;
import com.example.datn_qlnt_manager.utils.JwtUtil;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
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

    public String generateAccessToken(User user) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS256);

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getId()) // định danh user
                .issuer(ISSUER) // tên hệ thống
                .issueTime(Date.from(Instant.now())) // thời gian phát hành
                .expirationTime(Date.from(
                        Instant.now().plus(jwtUtil.getValidDuration(), ChronoUnit.SECONDS))) // thời gian hết hạn
                .claim(EMAIL_CLAIM, user.getEmail()) // custom claim
                .jwtID(UUID.randomUUID().toString()) // id
                .build();

        Payload payload = new Payload(
                jwtClaimsSet.toJSONObject()); // chuyển các claims sang json rồi bọc lại làm pay load cho JWT
        JWSObject jwsObject = new JWSObject(header, payload);

        try {
            jwsObject.sign(new MACSigner(jwtUtil.getSecretKey())); // ký token
            return jwsObject.serialize(); // trả về token dạng chuỗi
        } catch (JOSEException e) {
            throw new IllegalArgumentException(e);
        }
    }

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
                .build();

        var payload = new Payload(claimSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(header, payload);

        try {
            jwsObject.sign(new MACSigner(jwtUtil.getSecretKey()));
        } catch (JOSEException e) {
            throw new IllegalArgumentException(e);
        }

        return jwsObject.serialize();
    }

    public SignedJWT verifyToken(String token) {
        try {
            if (token.startsWith("Bearer")) {
                token = token.replace("Bearer ", "").trim();
            }

            SignedJWT signedJWT;

            try {
                signedJWT = SignedJWT.parse(token);
            } catch (ParseException e) {
                throw new AppException(ErrorCode.INVALID_TOKEN_FORMAT);
            }

            JWSVerifier verifier = new MACVerifier(jwtUtil.getSecretKey());

            boolean verified;
            try {
                verified = signedJWT.verify(verifier);
            } catch (JOSEException e) {
                throw new AppException(ErrorCode.INVALID_SIGNATURE);
            }

            if (!verified) {
                throw new AppException(ErrorCode.INVALID_SIGNATURE);
            }

            Date expiration = signedJWT.getJWTClaimsSet().getExpirationTime();
            if (expiration == null || expiration.before(Date.from(Instant.now()))) {
                throw new AppException(ErrorCode.EXPIRED_TOKEN);
            }

            String jti = signedJWT.getJWTClaimsSet().getJWTID();
            if (StringUtils.isNotBlank(redisService.get(jti))) {
                throw new AppException(ErrorCode.TOKEN_BLACKLISTED);
            }

            return signedJWT;

        } catch (AppException e) {
            throw e;
        } catch (Exception e) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
    }

    public String verifyAndExtractEmail(String token) throws ParseException {
        Object emailClaim = this.verifyToken(token).getJWTClaimsSet().getClaim(EMAIL_CLAIM);

        if (Objects.isNull(emailClaim)) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        return emailClaim.toString();
    }

    public String verifyAndExtractEmail(ServletServerHttpRequest request) throws ParseException {
        String token = request.getServletRequest().getHeader(HttpHeaders.AUTHORIZATION);
        Object emailClaim = this.verifyToken(token).getJWTClaimsSet().getClaim(EMAIL_CLAIM);

        if (Objects.isNull(emailClaim)) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        return emailClaim.toString();
    }

    public long verifyAndExtractTokenExpired(String token) throws ParseException {
        Date expiredClaim = this.verifyToken(token).getJWTClaimsSet().getExpirationTime();

        if (Objects.isNull(expiredClaim)) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        return expiredClaim.getTime();
    }
}
