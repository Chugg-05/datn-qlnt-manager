package com.example.datn_qlnt_manager.dto.request.feedback;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import com.example.datn_qlnt_manager.common.FeedbackStatus;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FeedbackStatusUpdateRequest {
    @NotBlank(message = "FEED_BACK_NOT_FOUND")
    String feedbackId;

    @NotNull(message = "FEED_BACK_STATUS_NOT_FOUND")
    FeedbackStatus feedbackStatus;

    @NotBlank(message = "NOTE_NOT_FOUND")
    String note;
}
