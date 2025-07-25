package com.example.datn_qlnt_manager.dto.filter;

import com.example.datn_qlnt_manager.common.FeedbackStatus;
import com.example.datn_qlnt_manager.common.FeedbackType;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FeedbackFilter {
    Integer rating;
    FeedbackType feedbackType;
    FeedbackStatus feedbackStatus;
    String query;
    String buildingId;
}
