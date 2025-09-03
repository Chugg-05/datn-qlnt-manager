package com.example.datn_qlnt_manager.controller;

import com.example.datn_qlnt_manager.common.FeedbackStatus;
import com.example.datn_qlnt_manager.common.FeedbackType;
import com.example.datn_qlnt_manager.configuration.Translator;
import com.example.datn_qlnt_manager.dto.PaginatedResponse;
import com.example.datn_qlnt_manager.dto.filter.FeedBackSelfFilter;
import com.example.datn_qlnt_manager.dto.filter.FeedbackFilter;
import com.example.datn_qlnt_manager.dto.request.feedback.*;
import jakarta.validation.Valid;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import com.example.datn_qlnt_manager.dto.ApiResponse;
import com.example.datn_qlnt_manager.dto.response.feedback.FeedbackResponse;
import com.example.datn_qlnt_manager.service.FeedbackService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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

    @Operation(summary = "Tiếp nhận phản hồi")
    @PutMapping("/start-processing/{feedbackId}")
    public ApiResponse<FeedbackResponse> startProcessing(@PathVariable("feedbackId") String feedbackId) {
        return ApiResponse.<FeedbackResponse>builder()
                .data(feedbackService.startProcessing(feedbackId))
                .message("Feedback marked as processing")
                .build();
    }

    @Operation(summary = "Đã xử lý phản hồi")
    @PutMapping("/complete-processing/{feedbackId}")
    public ApiResponse<FeedbackResponse> completeProcessing(@PathVariable("feedbackId") String feedbackId) {
        return ApiResponse.<FeedbackResponse>builder()
                .data(feedbackService.completeProcessing(feedbackId))
                .message("Feedback marked as completed")
                .build();
    }

    @Operation(summary = "Đánh giá phản hồi")
    @PutMapping("/rating/{feedbackId}")
    public ApiResponse<FeedbackResponse> rateFeedback(
            @PathVariable String feedbackId,
            @Valid @RequestBody FeedbackRatingRequest request) {

        return ApiResponse.<FeedbackResponse>builder()
                .data(feedbackService.rateFeedback(feedbackId, request))
                .message("Feedback rated successfully")
                .build();
    }

    @Operation(summary = "Khách gửi yêu cầu hỗ trợ thay đổi phương tiện")
    @PostMapping(value = "/change-vehicle", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<FeedbackResponse> changeVehicleFeedback(
            @Valid @ModelAttribute FeedbackChangeVehicleRequest request,
            @RequestParam(required = false) MultipartFile image) {
        return ApiResponse.<FeedbackResponse>builder()
                .data(feedbackService.changeVehicleFeedBack(request, image))
                .message("Change Vehicle Feedback Success")
                .build();
    }

    @Operation(summary = "Khách gửi yêu cầu hỗ trợ chấm dứt hợp đồng trước thời hạn và gia hạn")
    @PostMapping("/terminate-extend-contract")
    public ApiResponse<FeedbackResponse> terminateContract(
            @Valid @RequestBody FeedbackTerminateContractRequest request) {
        return ApiResponse.<FeedbackResponse>builder()
                .data(feedbackService.FeedbackTerminateContract(request))
                .message("Terminate Contract Success")
                .build();
    }
    @Operation(summary = "Khách gửi yêu cầu thêm phương tiện")
    @PostMapping("/create-vehicle")
    public ApiResponse<FeedbackResponse> createVehicleByTenant(
            @Valid @ModelAttribute FeedbackCreationVehicleRequest request,
            @RequestParam(required = false) MultipartFile vehicleRegistrationCard) {
        return ApiResponse.<FeedbackResponse>builder()
                .data(feedbackService.createFeedbackVehicleByTenant(request, vehicleRegistrationCard))
                .message("Create Vehicle Feedback Success")
                .build();
    }

    @Operation(summary = "Khách gửi yêu cầu xóa thành viên khỏi phòng")
    @PostMapping("/delete-tenant-not-rent")
    public ApiResponse<FeedbackResponse> createFeedBackDeleteTenantNotRent (
            @Valid @RequestBody FeedbackDeleteTenantRequest request
    ) {
        return ApiResponse.<FeedbackResponse>builder()
                .data(feedbackService.createFeedbackDeleteTenant(request))
                .message("Feedback has been sent")
                .build();
    }

    @Operation(summary = "Khách gửi yêu cầu đổi đại diện phòng")
    @PostMapping("/change-representative")
    public ApiResponse<FeedbackResponse> createFeedBackChangeRepresentative (
            @Valid @ModelAttribute FeedbackChangeRepresentativeRequest request,
            @RequestParam(required = false) List<MultipartFile> CCCD
    ) {
        return ApiResponse.<FeedbackResponse>builder()
                .data(feedbackService.changeRepreserntative(request, CCCD))
                .message("Feedback has been sent")
                .build();
    }


}
