package com.example.datn_qlnt_manager.dto.request.feedback;

import jakarta.validation.constraints.*;

import com.example.datn_qlnt_manager.common.FeedbackStatus;
import com.example.datn_qlnt_manager.common.FeedbackType;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FeedbackUpdateRequest {

    @NotBlank(message = "CONTENT_NOT_FOUND")
    String content;

    @NotNull(message = "FEED_BACK_TYPE_NOT_FOUND")
    FeedbackType feedbackType;

    @NotNull(message = "FEED_BACK_STATUS_NOT_FOUND")
    FeedbackStatus feedbackStatus;

    @NotNull(message = "RATING_NOT_FOUND")
    @Min(value = 1, message = "RATING_TOO_LOW")
    @Max(value = 5, message = "RATING_TOO_HIGH")
    Integer rating;

    String attachment;
}
