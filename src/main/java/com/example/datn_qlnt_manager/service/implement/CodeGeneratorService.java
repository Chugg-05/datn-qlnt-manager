package com.example.datn_qlnt_manager.service.implement;

import com.example.datn_qlnt_manager.utils.CodeGeneratorUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CodeGeneratorService {
    RedisTemplate<String, Object> redisTemplate;

    static String REDIS_KEY_PREFIX = "codegen:";

    public String generateCode(String type, String rawName) {
        String prefix = CodeGeneratorUtil.generatePrefixFromName(rawName);
        long nextNumber = getNextNumber(type, prefix);
        return prefix + String.format("%06d", nextNumber);
    }

    private long getNextNumber(String type, String prefix) {
        String redisKey = REDIS_KEY_PREFIX + type + ":" + prefix;
        Long value = redisTemplate.opsForValue().increment(redisKey, 1);
        if (value == null) {
            throw new IllegalStateException("Failed to increment Redis key: " + redisKey);
        }
        return value;
    }
}
