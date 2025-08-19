package com.example.datn_qlnt_manager.dto.response.notification;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class IdAndName {
    String id;
    String fullName;

}
