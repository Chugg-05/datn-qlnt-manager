package com.example.datn_qlnt_manager.dto.statistics;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserStatistics {
    Long totalUsers;
    Long totalActiveUsers;
    Long totalExpiredUsers;
    Long totalLockedUsers;
    Long totalDeletedUsers;
}
