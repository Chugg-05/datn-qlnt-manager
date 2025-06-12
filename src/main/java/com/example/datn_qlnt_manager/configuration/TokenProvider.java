package com.example.datn_qlnt_manager.configuration;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.security.interfaces.RSAPublicKey;
import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

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
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
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

    @Value("${google.client.id}")
    protected String CLIENT_ID;

    private static final String GOOGLE_JWK_URL = "https://www.googleapis.com/oauth2/v3/certs";
    private static final String EXPECTED_ISSUER = "https://accounts.google.com";

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

    //    public String verifyAndExtractEmail(ServletServerHttpRequest request) throws ParseException {
    //        String token = request.getServletRequest().getHeader(HttpHeaders.AUTHORIZATION);
    //        Object emailClaim = this.verifyToken(token).getJWTClaimsSet().getClaim(EMAIL_CLAIM);
    //
    //        if (Objects.isNull(emailClaim)) {
    //            throw new AppException(ErrorCode.UNAUTHORIZED);
    //        }
    //
    //        return emailClaim.toString();
    //    }

    public long verifyAndExtractTokenExpired(String token) throws ParseException {
        Date expiredClaim = this.verifyToken(token).getJWTClaimsSet().getExpirationTime();

        if (Objects.isNull(expiredClaim)) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        return expiredClaim.getTime();
    }

    // Xác thực token đăng nhập từ gg
    public Map<String, Object> verifyTokenIdGoogle(String token) throws ParseException, IOException, JOSEException {
        SignedJWT signedJWT = SignedJWT.parse(token); // giải mã ID Token đc truyền vào
        String keyId = signedJWT.getHeader().getKeyID(); // lấy key trong JWT Header

        InputStream is = URI.create(GOOGLE_JWK_URL).toURL().openStream();
        JWKSet jwkSet = JWKSet.load(is); // tải ds pubkey của gg

        JWK jwk = jwkSet.getKeyByKeyId(keyId); // tìm đúng public key với keyId từ token
        if (jwk == null) {
            log.error("Không tìm thấy public key tương ứng: " + keyId);
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        RSAKey rsaKey = (RSAKey) jwk; // Convert JWK về RSAKey để láy RSAPublicKey
        RSAPublicKey publicKey = rsaKey.toRSAPublicKey();

        // xác minh chữ ký JWT có khớp với RSAPublicKey không
        boolean isVerified = signedJWT.verify(new RSASSAVerifier(publicKey));

        if (!isVerified) {
            log.error("❌ Token không hợp lệ (chữ ký sai).");
            throw new AppException(ErrorCode.INVALID_TOKEN);
        }

        Map<String, Object> claims = signedJWT.getJWTClaimsSet().getClaims();

        if (!EXPECTED_ISSUER.equals(claims.get("iss"))) {
            log.error("❌ Sai issuer.");
            throw new AppException(ErrorCode.INVALID_ISSUER);
        }

        if (!CLIENT_ID.equals(((java.util.List<?>) claims.get("aud")).get(0))) {
            log.error("❌ Sai audience.");
            throw new AppException(ErrorCode.INVALID_AUDIENCE);
        }

        long exp = signedJWT.getJWTClaimsSet().getExpirationTime().getTime(); // lấy thời gian hết hạn để kiểm tra

        if (System.currentTimeMillis() > exp) {
            log.error("❌ Token đã hết hạn.");
            throw new AppException(ErrorCode.TOKEN_BLACKLISTED);
        }

        return claims;
    }
}
