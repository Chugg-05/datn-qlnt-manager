package com.example.datn_qlnt_manager.service;

import com.example.datn_qlnt_manager.dto.PaginatedResponse;
import com.example.datn_qlnt_manager.dto.filter.FeedBackSelfFilter;
import com.example.datn_qlnt_manager.dto.filter.FeedbackFilter;
import com.example.datn_qlnt_manager.dto.request.feedback.*;
import com.example.datn_qlnt_manager.dto.response.feedback.FeedbackResponse;
import org.springframework.web.multipart.MultipartFile;

public interface FeedbackService {
    FeedbackResponse createFeedback(FeedbackCreationRequest request);

    PaginatedResponse<FeedbackResponse> filterMyFeedbacks(FeedBackSelfFilter filter, int page, int size);

    PaginatedResponse<FeedbackResponse> filterFeedbacksForManager(FeedbackFilter filter, int page, int size);

    FeedbackResponse rejectFeedback(String feedbackId, RejectFeedbackRequest request);

    FeedbackResponse startProcessing(String feedbackId);

    FeedbackResponse completeProcessing(String feedbackId);

    FeedbackResponse changeVehicleFeedBack(FeedbackChangeVehicleRequest request, MultipartFile image);

    FeedbackResponse FeedbackTerminateContract(FeedbackTerminateContractRequest request);

    FeedbackResponse rateFeedback(String feedbackId,FeedbackRatingRequest request);
}
