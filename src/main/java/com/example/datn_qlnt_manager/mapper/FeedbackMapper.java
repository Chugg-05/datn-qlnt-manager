package com.example.datn_qlnt_manager.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.example.datn_qlnt_manager.dto.request.feedback.FeedbackCreationRequest;
import com.example.datn_qlnt_manager.dto.request.feedback.FeedbackUpdateRequest;
import com.example.datn_qlnt_manager.dto.response.feedback.FeedbackResponse;
import com.example.datn_qlnt_manager.entity.Feedback;

@Mapper(componentModel = "spring")
public interface FeedbackMapper {
    @Mapping(target = "feedbackStatus", constant = "CHUA_XU_LY")
    Feedback toEntity(FeedbackCreationRequest request);

    void updateFeedback(@MappingTarget Feedback feedback, FeedbackUpdateRequest request);

    FeedbackResponse toResponse(Feedback feedback);
}
