package com.example.datn_qlnt_manager.service.implement;


import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class GoogleJwkCacheService {
    private static final String GOOGLE_JWK_URL = "https://www.googleapis.com/oauth2/v3/certs"; // URL để lấy JWK từ Google
    private final Map<String, JWK> jwkCache = new ConcurrentHashMap<>(); // Lưu trữ JWK theo key ID
    private Instant lastFetched = Instant.MIN; // Thời điểm lần cuối cập nhật cache

    // Phương thức để lấy JWK theo key ID
    public Optional<JWK> getJwkByKeyId(String keyId) {
        // Kiểm tra nếu cache đã cũ hoặc rỗng
        if (Duration.between(lastFetched, Instant.now()).toMinutes() > 10 || jwkCache.isEmpty()) {
            refreshJwkCache();
        }

        // Trả về JWK nếu có trong cache, nếu không trả về Optional.empty()
        return Optional.ofNullable(jwkCache.get(keyId));
    }

    // Phương thức để làm mới cache JWK
    @Scheduled(fixedDelay = 10 * 60 * 1000) // Cập nhật cache mỗi 10 phút
    public void refreshJwkCache() {
        try (InputStream is = URI.create(GOOGLE_JWK_URL).toURL().openStream() ) {
            JWKSet jwkSet = JWKSet.load(is); // Tải JWK từ URL
            Map<String, JWK> tempMap = new ConcurrentHashMap<>(); // Tạo map tạm thời để lưu trữ JWK
            for (JWK jwk : jwkSet.getKeys()) { // Duyệt qua từng JWK
                tempMap.put(jwk.getKeyID(), jwk);
            }
            jwkCache.clear(); // Xóa cache cũ
            jwkCache.putAll(tempMap); // Cập nhật cache mới
            lastFetched = Instant.now(); // Cập nhật thời điểm lấy JWK mới
            log.info("Đã refresh cache Google JWK thành công.");
        } catch (Exception e) {
            log.warn("Không thể tải Google JWK: {}", e.getMessage(), e);
        }
    }
}
