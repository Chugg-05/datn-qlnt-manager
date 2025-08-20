package com.example.datn_qlnt_manager.dto.filter;

import com.example.datn_qlnt_manager.common.DepositStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DepositFilter {
    String query;
    String building;
    String floor;
    String room;
    DepositStatus depositStatus;

}
