package com.example.datn_qlnt_manager.dto.response.feedback;

import java.time.Instant;

import com.example.datn_qlnt_manager.common.FeedbackStatus;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FeedbackStatusUpdateResponse {
    String id;
    String feedbackId;
    String content;
    FeedbackStatus feedbackStatus;
    String updatedBy;
    String note;
    Instant updatedAt;
}
