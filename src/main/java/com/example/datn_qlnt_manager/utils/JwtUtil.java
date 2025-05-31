package com.example.datn_qlnt_manager.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {
    @Value("${jwt.secretKey}")
    protected String secretKey;

    @Value("${jwt.valid-duration}")
    protected Long VALID_DURATION;

    @Value("${jwt.refreshable-duration}")
    protected Long REFRESHABLE_DURATION;

    public byte[] getSecretKey() {
        return secretKey.getBytes();
    }

    public Long getValidDuration() {
        return VALID_DURATION;
    }

    public Long getRefreshableDuration() {
        return REFRESHABLE_DURATION;
    }
}
