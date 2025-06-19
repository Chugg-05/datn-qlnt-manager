package com.example.datn_qlnt_manager.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
// Lớp này cấu hình các STOMP endpoint và trình điều khiển message.
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    // Đăng ký các endpoint STOMP để client có thể kết nối.
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Đăng ký endpoint "/ws" và cho phép kết nối
        registry.addEndpoint("/ws").setAllowedOrigins("http://localhost:3000").withSockJS();
    }

    // Cấu hình message broker để xử lý các tin nhắn STOMP.
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/app"); // Tiền tố cho các destination của ứng dụng
        registry.enableSimpleBroker("/", "/"); // Kích hoạt message broker đơn giản với các destination gốc
        registry.setUserDestinationPrefix("/user"); // Tiền tố cho các destination của người dùng
    }
}
