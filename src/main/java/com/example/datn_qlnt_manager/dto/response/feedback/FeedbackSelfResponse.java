package com.example.datn_qlnt_manager.dto.response.feedback;

import java.time.Instant;

import com.example.datn_qlnt_manager.common.FeedbackStatus;
import com.example.datn_qlnt_manager.common.FeedbackType;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FeedbackSelfResponse {
    String fullName;
    String roomCode;
    String content;
    Integer rating;
    FeedbackStatus feedbackStatus;
    FeedbackType feedbackType;
    String attachment;
    Instant createdAt;
    Instant updatedAt;
}
