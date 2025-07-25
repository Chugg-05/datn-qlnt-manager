package com.example.datn_qlnt_manager.dto.response.feedback;

import java.time.Instant;

import com.example.datn_qlnt_manager.common.FeedbackStatus;
import com.example.datn_qlnt_manager.common.FeedbackType;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FeedbackResponse {
    String id;

    String tenantId;
    String fullName;

    String roomId;
    String roomCode;

    String content;

    FeedbackType feedbackType;

    Integer rating;

    String attachment;

    FeedbackStatus feedbackStatus;

    Instant createdAt;

    Instant updatedAt;
}
