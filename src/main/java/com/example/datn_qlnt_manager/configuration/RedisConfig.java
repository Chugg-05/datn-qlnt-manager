package com.example.datn_qlnt_manager.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RedisConfig {
    // Lấy giá trị host Redis từ file cấu hình (application.yml)
    @Value("${spring.data.redis.host}")
    String redisHost; // Lấy giá trị port Redis từ file cấu hình

    @Value("${spring.data.redis.port}")
    int redisPort; // Lấy username Redis (nếu có) từ file cấu hình

    @Value("${spring.data.redis.username}")
    String redisUsername; // Lấy password Redis từ file cấu hình

    @Value("${spring.data.redis.password}")
    String redisPassword;

    /**
     *
     * Bean LettuceConnectionFactory để kết nối đến Redis server.
     *
     * Sử dụng RedisStandaloneConfiguration để cấu hình thông tin kết nối.
     *
     * Gán hostname, port, username và password cho cấu hình Redis.
     *
     */
    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {

        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(redisHost);

        config.setPort(redisPort);

        config.setUsername(redisUsername);

        config.setPassword(redisPassword);

        // Trả về factory để Spring Data Redis sử dụng kết nối Lettuce
        return new LettuceConnectionFactory(config);
    }

    /**
     *
     * Bean RedisTemplate dùng để thao tác với Redis.
     *
     * Định nghĩa các serializer cho key và value giúp dữ liệu lưu trữ dễ đọc và tránh lỗi.
     *
     */
    @Bean
    @Primary
    public RedisTemplate<String, Object> redisTemplate() {

        RedisTemplate<String, Object> template = new RedisTemplate<>();
        // Set connection factory cho RedisTemplate

        template.setConnectionFactory(redisConnectionFactory());
        // Chuyển đổi key thành String để dễ đọc và debug

        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        // Chuyển đổi value sang JSON giúp dễ đọc và tránh lỗi serialization

        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }
}
