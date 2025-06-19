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
    // Lấy giá trị host Redis
    @Value("${spring.data.redis.host}")
    String redisHost;

    // Lấy giá trị port Redis
    @Value("${spring.data.redis.port}")
    int redisPort;

    // Lấy giá trị username Redis
    @Value("${spring.data.redis.username}")
    String redisUsername;

    // Lấy giá trị password Redis
    @Value("${spring.data.redis.password}")
    String redisPassword;

    // Tạo kết nối Redis sử dụng LettuceConnectionFactory
    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {

        // Cấu hình kết nối Redis với các thông số như host, port, username và password
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(redisHost);

        config.setPort(redisPort);

        config.setUsername(redisUsername);

        config.setPassword(redisPassword);

        // Trả về factory để Spring Data Redis sử dụng kết nối LettuceConnectionFactory
        return new LettuceConnectionFactory(config);
    }

    // Tạo RedisTemplate để sử dụng trong ứng dụng
    @Bean
    @Primary
    public RedisTemplate<String, Object> redisTemplate() {

        // Tạo một RedisTemplate với kiểu dữ liệu String cho key và Object cho value
        RedisTemplate<String, Object> template = new RedisTemplate<>();

        // Thiết lập kết nối Redis cho RedisTemplate
        template.setConnectionFactory(redisConnectionFactory());

        // Thiết lập serializer cho key và value của RedisTemplate
        template.setKeySerializer(new StringRedisSerializer());

        // Thiết lập serializer cho key trong hash
        template.setHashKeySerializer(new StringRedisSerializer());

        // Sử dụng GenericJackson2JsonRedisSerializer để serialize và deserialize giá trị
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());

        // Thiết lập serializer cho giá trị trong hash
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());

        // Khởi tạo RedisTemplate
        return template;
    }
}
