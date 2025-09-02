package com.example.datn_qlnt_manager.dto.response.feedback;

import java.time.Instant;

import com.example.datn_qlnt_manager.common.FeedbackStatus;
import com.example.datn_qlnt_manager.common.FeedbackType;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FeedbackResponse {
    String id;
    String nameSender;
    String roomCode;
    String content;
    FeedbackType feedbackType;
    Integer rating;
    String attachment;
    FeedbackStatus feedbackStatus;
    String rejectionReason;
    Instant createdAt;
    Instant updatedAt;
}
