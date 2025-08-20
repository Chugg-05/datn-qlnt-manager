package com.example.datn_qlnt_manager.dto.response.deposit;

import com.example.datn_qlnt_manager.common.DepositStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ConfirmDepositResponse {
    String id;
    DepositStatus depositStatus;
}
