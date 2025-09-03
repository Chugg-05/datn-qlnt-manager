package com.example.datn_qlnt_manager.dto.request.feedback;

import jakarta.validation.constraints.*;

import com.example.datn_qlnt_manager.common.FeedbackType;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FeedbackCreationRequest {

    @NotBlank(message = "ROOM_NOT_FOUND")
    String roomId;

    @NotBlank(message = "FEEDBACK_NAME_NOT_BLANK")
    String feedbackName;

    @NotBlank(message = "CONTENT_NOT_FOUND")
    String content;

    @NotNull(message = "FEED_BACK_TYPE_NOT_FOUND")
    FeedbackType feedbackType;

    String attachment;
}
