package com.example.datn_qlnt_manager.controller;

import com.example.datn_qlnt_manager.common.FeedbackStatus;
import com.example.datn_qlnt_manager.common.FeedbackType;
import com.example.datn_qlnt_manager.dto.ApiResponse;
import com.example.datn_qlnt_manager.dto.PaginatedResponse;
import com.example.datn_qlnt_manager.dto.filter.FeedBackSelfFilter;
import com.example.datn_qlnt_manager.dto.filter.FeedbackFilter;
import com.example.datn_qlnt_manager.dto.request.feedback.FeedbackCreationRequest;
import com.example.datn_qlnt_manager.dto.request.feedback.FeedbackStatusUpdateRequest;
import com.example.datn_qlnt_manager.dto.request.feedback.FeedbackUpdateRequest;
import com.example.datn_qlnt_manager.dto.response.feedback.FeedbackResponse;
import com.example.datn_qlnt_manager.dto.response.feedback.FeedbackSelfResponse;
import com.example.datn_qlnt_manager.dto.response.feedback.FeedbackStatusUpdateResponse;
import com.example.datn_qlnt_manager.service.FeedbackService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

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
                .message("Feedback created successfully")
                .build();
    }

    @Operation(summary = "Sửa phản hồi")
    @PutMapping("/{feedbackId}")
    public ApiResponse<FeedbackResponse> updateFeedback(
            @PathVariable String feedbackId,
            @Valid @RequestBody FeedbackUpdateRequest request) {
        return ApiResponse.<FeedbackResponse>builder()
                .data(feedbackService.updateFeedback(feedbackId,request))
                .message("Feedback created successfully")
                .build();
    }

    @Operation(summary = "Khách thuê xem, tìm kiếm và lọc phản hồi của họ")
    @GetMapping("/my-feedbacks")
    public ApiResponse<PaginatedResponse<FeedbackSelfResponse>> filterMyFeedbacks(
            @RequestParam(required = false) Integer rating,
            @RequestParam(required = false) FeedbackType feedbackType,
            @RequestParam(required = false) FeedbackStatus feedbackStatus,
            @RequestParam(required = false) String query,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "15") int size
    ) {
        FeedBackSelfFilter filter = new FeedBackSelfFilter(rating, feedbackType, feedbackStatus, query);
        return ApiResponse.<PaginatedResponse<FeedbackSelfResponse>>builder()
                .data(feedbackService.filterMyFeedbacks(filter, page, size))
                .message("List of feedbacks by current tenant loaded successfully.")
                .build();
    }
    @Operation(summary = "Quản lý xem, tìm kiếm ,lọc phản hồi theo các tòa nhà của họ")
    @GetMapping("/find-all")
    public ApiResponse<PaginatedResponse<FeedbackResponse>> filterFeedbacksForManager(
            @RequestParam(required = false) String buildingId,
            @RequestParam(required = false) String query,
            @RequestParam(required = false) FeedbackStatus feedbackStatus,
            @RequestParam(required = false) Integer rating,
            @RequestParam(required = false) FeedbackType feedbackType,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "15") int size
    ) {
        FeedbackFilter filter = FeedbackFilter.builder()
                .buildingId(buildingId)
                .query(query)
                .feedbackStatus(feedbackStatus)
                .rating(rating)
                .feedbackType(feedbackType)
                .build();
        return ApiResponse.<PaginatedResponse<FeedbackResponse>>builder()
                .data(feedbackService.filterFeedbacksForManager(filter, page, size))
                .message("Filter feedbacks for manager loaded successfully")
                .build();
    }

    @Operation(summary = "Xác nhận hoặc cập nhật trạng thái phản hồi")
    @PutMapping("/update-status")
    public ApiResponse<FeedbackStatusUpdateResponse> updateFeedbackStatus(
            @Valid @RequestBody FeedbackStatusUpdateRequest request) {
        return ApiResponse.<FeedbackStatusUpdateResponse>builder()
                .data(feedbackService.updateFeedbackStatus(request))
                .message("Feedback status updated successfully")
                .build();
    }

}
