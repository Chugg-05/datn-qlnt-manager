package com.example.datn_qlnt_manager.dto.statistics;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ServiceRoomStatusStatistics {
    Long total;
    Long active;
    Long paused;
    Long canceled;
}
