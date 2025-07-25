package com.example.datn_qlnt_manager.dto.response.feedbackProcessHistory;

import java.time.Instant;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FeedbackProcessHistoryResponse {
    String id;
    String feedbackId;
    String content;
    String userId;
    String fullName;
    String note;
    Instant time;
}
