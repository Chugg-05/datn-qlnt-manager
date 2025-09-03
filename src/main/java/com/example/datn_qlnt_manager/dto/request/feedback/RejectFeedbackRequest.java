package com.example.datn_qlnt_manager.dto.request.feedback;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RejectFeedbackRequest {
    @NotBlank(message = "REJECT_REASON_CANNOT_BLANK")
    String rejectionReason;
}