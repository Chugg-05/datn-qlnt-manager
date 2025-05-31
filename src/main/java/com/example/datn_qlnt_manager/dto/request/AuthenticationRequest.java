package com.example.datn_qlnt_manager.dto.request;

import java.io.Serializable;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthenticationRequest implements Serializable {
    String email;
    String password;
}
