package com.example.datn_qlnt_manager.controller;

import com.example.datn_qlnt_manager.common.FeedbackStatus;
import com.example.datn_qlnt_manager.common.FeedbackType;
import com.example.datn_qlnt_manager.configuration.Translator;
import com.example.datn_qlnt_manager.dto.PaginatedResponse;
import com.example.datn_qlnt_manager.dto.filter.FeedBackSelfFilter;
import com.example.datn_qlnt_manager.dto.filter.FeedbackFilter;
import com.example.datn_qlnt_manager.dto.request.feedback.RejectFeedbackRequest;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;

import com.example.datn_qlnt_manager.dto.ApiResponse;
import com.example.datn_qlnt_manager.dto.request.feedback.FeedbackCreationRequest;
import com.example.datn_qlnt_manager.dto.request.feedback.FeedbackStatusUpdateRequest;
import com.example.datn_qlnt_manager.dto.request.feedback.FeedbackUpdateRequest;
import com.example.datn_qlnt_manager.dto.response.feedback.FeedbackResponse;
import com.example.datn_qlnt_manager.service.FeedbackService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/feed-backs")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FeedbackController {

    FeedbackService feedbackService;

    @Operation(summary = "Thêm phản hồi")
    @PostMapping
    public ApiResponse<FeedbackResponse> createFeedback(@Valid @RequestBody FeedbackCreationRequest request) {
        return ApiResponse.<FeedbackResponse>builder()
                .data(feedbackService.createFeedback(request))
                .message(Translator.toLocale("feedback.created.successfully"))
                .build();
    }

    @Operation(summary = "Sửa phản hồi")
    @PutMapping("/{feedbackId}")
    public ApiResponse<FeedbackResponse> updateFeedback(
            @PathVariable String feedbackId, @Valid @RequestBody FeedbackUpdateRequest request) {
        return ApiResponse.<FeedbackResponse>builder()
                .data(feedbackService.updateFeedback(feedbackId, request))
                .message(Translator.toLocale("feedback.updated.success"))
                .build();
    }

    @Operation(summary = "Khách thuê xem, tìm kiếm và lọc phản hồi của họ")
    @GetMapping("/my-feedbacks")
    public ApiResponse<PaginatedResponse<FeedbackResponse>> filterMyFeedbacks(
            @RequestParam(required = false) Integer rating,
            @RequestParam(required = false) FeedbackType feedbackType,
            @RequestParam(required = false) FeedbackStatus feedbackStatus,
            @RequestParam(required = false) String query,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "15") int size) {
        FeedBackSelfFilter filter = new FeedBackSelfFilter(rating, feedbackType, feedbackStatus, query);
        return ApiResponse.<PaginatedResponse<FeedbackResponse>>builder()
                .data(feedbackService.filterMyFeedbacks(filter, page, size))
                .message(Translator.toLocale("list.of.feedbacks.by.current.tenant.loaded.success"))
                .build();
    }

    @Operation(summary = "Chủ xem, tìm kiếm ,lọc phản hồi theo các tòa nhà của họ")
    @GetMapping("/find-all")
    public ApiResponse<PaginatedResponse<FeedbackResponse>> filterFeedbacksForManager(
            @RequestParam(required = false) String buildingId,
            @RequestParam(required = false) String query,
            @RequestParam(required = false) FeedbackStatus feedbackStatus,
            @RequestParam(required = false) Integer rating,
            @RequestParam(required = false) FeedbackType feedbackType,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "15") int size) {
        FeedbackFilter filter = FeedbackFilter.builder()
                .buildingId(buildingId)
                .query(query)
                .feedbackStatus(feedbackStatus)
                .rating(rating)
                .feedbackType(feedbackType)
                .build();
        return ApiResponse.<PaginatedResponse<FeedbackResponse>>builder()
                .data(feedbackService.filterFeedbacksForManager(filter, page, size))
                .message(Translator.toLocale("filter.feedbacks.for.manager.loaded.success"))
                .build();
    }

    @Operation(summary = "Xác nhận phản hồi")
    @PutMapping("/update-status")
    public ApiResponse<FeedbackResponse> updateFeedbackStatus(
            @Valid @RequestBody FeedbackStatusUpdateRequest request) {
        return ApiResponse.<FeedbackResponse>builder()
                .data(feedbackService.updateFeedbackStatus(request))
                .message(Translator.toLocale("feedback.status.updated.success"))
                .build();
    }

    @Operation(summary = "Từ chối yêu cầu")
    @PutMapping("/reject/{feedbackId}")
    public ApiResponse<FeedbackResponse> rejectFeedback(
            @PathVariable String feedbackId,
            @Valid @RequestBody RejectFeedbackRequest request) {
        return ApiResponse.<FeedbackResponse>builder()
                .data(feedbackService.rejectFeedback(feedbackId, request))
                .message("Feedback Rejected Success")
                .build();
    }

}
