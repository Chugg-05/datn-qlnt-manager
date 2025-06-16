package com.example.datn_qlnt_manager.dto.filter;

import com.example.datn_qlnt_manager.common.Gender;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserFilter {
    String name;
    String email;
    String phone;
    String address;
    Gender gender;
}
