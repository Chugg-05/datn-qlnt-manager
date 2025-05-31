package com.example.datn_qlnt_manager.dto.response;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LoginResponse implements Serializable {
    String accessToken;
    Long accessTokenTTL;
    String refreshToken;
    Long refreshTokenTTL;
    String userId;
}
