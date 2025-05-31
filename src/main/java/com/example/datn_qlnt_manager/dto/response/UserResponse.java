package com.example.datn_qlnt_manager.dto.response;

import java.io.Serializable;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse implements Serializable {
    String id;
    String fullName;
    String email;
    String profilePicture;
}
