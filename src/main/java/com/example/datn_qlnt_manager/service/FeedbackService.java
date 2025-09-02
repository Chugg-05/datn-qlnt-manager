package com.example.datn_qlnt_manager.service;

import com.example.datn_qlnt_manager.dto.PaginatedResponse;
import com.example.datn_qlnt_manager.dto.filter.FeedBackSelfFilter;
import com.example.datn_qlnt_manager.dto.filter.FeedbackFilter;
import com.example.datn_qlnt_manager.dto.request.feedback.FeedbackCreationRequest;
import com.example.datn_qlnt_manager.dto.request.feedback.FeedbackStatusUpdateRequest;
import com.example.datn_qlnt_manager.dto.request.feedback.FeedbackUpdateRequest;
import com.example.datn_qlnt_manager.dto.request.feedback.RejectFeedbackRequest;
import com.example.datn_qlnt_manager.dto.response.feedback.FeedbackResponse;

public interface FeedbackService {
    FeedbackResponse createFeedback(FeedbackCreationRequest request);

    FeedbackResponse updateFeedback(String feedbackId, FeedbackUpdateRequest request);

    PaginatedResponse<FeedbackResponse> filterMyFeedbacks(FeedBackSelfFilter filter, int page, int size);

    PaginatedResponse<FeedbackResponse> filterFeedbacksForManager(FeedbackFilter filter, int page, int size);

FeedbackResponse updateFeedbackStatus(FeedbackStatusUpdateRequest request);

    FeedbackResponse rejectFeedback(String feedbackId, RejectFeedbackRequest request);
}
