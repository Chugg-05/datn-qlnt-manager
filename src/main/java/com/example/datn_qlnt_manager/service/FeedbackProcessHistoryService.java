package com.example.datn_qlnt_manager.service;

import com.example.datn_qlnt_manager.dto.PaginatedResponse;
import com.example.datn_qlnt_manager.dto.response.feedbackProcessHistory.FeedbackProcessHistoryResponse;

public interface FeedbackProcessHistoryService {
    PaginatedResponse<FeedbackProcessHistoryResponse> getAllByUserId(
            String feedbackId, String query, int page, int size);
}
