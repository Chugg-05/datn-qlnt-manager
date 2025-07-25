package com.example.datn_qlnt_manager.service.implement;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.example.datn_qlnt_manager.service.RedisService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RedisServiceImpl implements RedisService {
    RedisTemplate<String, Object> redisTemplate;

    @Override
    public void save(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }

    @Override
    public void save(String key, String value, long seconds, TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, value, seconds, timeUnit);
    }

    @Override
    public String get(String key) {
        return (String) redisTemplate.opsForValue().get(key);
    }

    @Override
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    @Override
    public long increment(String key) {
        Long index = redisTemplate.opsForValue().increment(key, 1);
        if (index == null) {
            throw new IllegalStateException("Không thể tăng giá trị Redis: " + key);
        }
        return index;
    }

    @Override
    public boolean exists(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    @Override
    public void markAsUsed(String key) {
        redisTemplate.opsForValue().set(key, true);
    }
}
