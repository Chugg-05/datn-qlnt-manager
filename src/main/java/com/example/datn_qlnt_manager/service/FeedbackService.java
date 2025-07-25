package com.example.datn_qlnt_manager.service;

import com.example.datn_qlnt_manager.dto.PaginatedResponse;
import com.example.datn_qlnt_manager.dto.filter.FeedBackSelfFilter;
import com.example.datn_qlnt_manager.dto.filter.FeedbackFilter;
import com.example.datn_qlnt_manager.dto.request.feedback.FeedbackCreationRequest;
import com.example.datn_qlnt_manager.dto.request.feedback.FeedbackStatusUpdateRequest;
import com.example.datn_qlnt_manager.dto.request.feedback.FeedbackUpdateRequest;
import com.example.datn_qlnt_manager.dto.response.feedback.FeedbackResponse;
import com.example.datn_qlnt_manager.dto.response.feedback.FeedbackSelfResponse;
import com.example.datn_qlnt_manager.dto.response.feedback.FeedbackStatusUpdateResponse;

public interface FeedbackService {
    FeedbackResponse createFeedback(FeedbackCreationRequest request);

    FeedbackResponse updateFeedback(String feedbackId, FeedbackUpdateRequest request);

    PaginatedResponse<FeedbackSelfResponse> filterMyFeedbacks(FeedBackSelfFilter filter, int page, int size);

    PaginatedResponse<FeedbackResponse> filterFeedbacksForManager(FeedbackFilter filter, int page, int size);

    FeedbackStatusUpdateResponse updateFeedbackStatus(FeedbackStatusUpdateRequest request);
}
