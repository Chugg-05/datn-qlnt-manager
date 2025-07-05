package com.example.datn_qlnt_manager.service.implement;

import com.example.datn_qlnt_manager.common.FeedbackStatus;
import com.example.datn_qlnt_manager.common.Meta;
import com.example.datn_qlnt_manager.common.Pagination;
import com.example.datn_qlnt_manager.dto.PaginatedResponse;
import com.example.datn_qlnt_manager.dto.filter.FeedBackSelfFilter;
import com.example.datn_qlnt_manager.dto.filter.FeedbackFilter;
import com.example.datn_qlnt_manager.dto.request.feedback.FeedbackCreationRequest;
import com.example.datn_qlnt_manager.dto.request.feedback.FeedbackStatusUpdateRequest;
import com.example.datn_qlnt_manager.dto.request.feedback.FeedbackUpdateRequest;
import com.example.datn_qlnt_manager.dto.response.feedback.FeedbackResponse;
import com.example.datn_qlnt_manager.dto.response.feedback.FeedbackSelfResponse;
import com.example.datn_qlnt_manager.dto.response.feedback.FeedbackStatusUpdateResponse;
import com.example.datn_qlnt_manager.entity.*;
import com.example.datn_qlnt_manager.exception.AppException;
import com.example.datn_qlnt_manager.exception.ErrorCode;
import com.example.datn_qlnt_manager.mapper.FeedbackMapper;
import com.example.datn_qlnt_manager.repository.FeedbackProcessHistoryRepository;
import com.example.datn_qlnt_manager.repository.FeedbackRepository;
import com.example.datn_qlnt_manager.repository.RoomRepository;
import com.example.datn_qlnt_manager.repository.TenantRepository;
import com.example.datn_qlnt_manager.service.FeedbackService;
import com.example.datn_qlnt_manager.service.UserService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FeedbackServiceImpl implements FeedbackService {

    FeedbackRepository feedbackRepository;
    TenantRepository tenantRepository;
    RoomRepository roomRepository;
    FeedbackMapper feedbackMapper;
    UserService userService;
    FeedbackProcessHistoryRepository feedbackProcessHistoryRepository;

    @Override
    public FeedbackResponse createFeedback(FeedbackCreationRequest request) {
        Tenant tenant = tenantRepository.findById(request.getTenantId())
                .orElseThrow(() -> new AppException(ErrorCode.TENANT_NOT_FOUND));

        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_FOUND));

        String normalizedContent = normalizeContent(request.getContent());
        List<Feedback> existingFeedbacks = feedbackRepository
                .findAllByTenantIdAndRoomIdAndFeedbackTypeAndFeedbackStatusIn(
                        tenant.getId(),
                        room.getId(),
                        request.getFeedbackType(),
                        List.of(FeedbackStatus.CHUA_XU_LY)
                );
        boolean isDuplicate = existingFeedbacks.stream().anyMatch(f ->
                f.getContent() != null &&
                        normalizeContent(f.getContent()).equals(normalizedContent)
        );

        if (isDuplicate) {
            throw new AppException(ErrorCode.FEED_BACK_DUPLICATED);
        }

        Feedback feedback = feedbackMapper.toEntity(request);
        feedback.setTenant(tenant);
        feedback.setRoom(room);
        feedback.setCreatedAt(Instant.now());
        feedback.setUpdatedAt(Instant.now());

        return feedbackMapper.toResponse(feedbackRepository.save(feedback));
    }

    @Override
    public FeedbackResponse updateFeedback(String feedbackId, FeedbackUpdateRequest request) {
        Feedback feedback = feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new AppException(ErrorCode.FEED_BACK_NOT_FOUND));

        feedbackMapper.updateFeedback(feedback, request);
        feedback.setUpdatedAt(Instant.now());

        return feedbackMapper.toResponse(feedbackRepository.save(feedback));
    }

    @Override
    public PaginatedResponse<FeedbackSelfResponse> filterMyFeedbacks(FeedBackSelfFilter filter, int page, int size) {
        String currentUserId = userService.getCurrentUser().getId();

        Pageable pageable = PageRequest.of(Math.max(0, page - 1), size, Sort.by(Sort.Direction.DESC, "updatedAt"));

        Page<FeedbackSelfResponse> pageResult = feedbackRepository.findByTenantOwnerIdWithFilter(
                currentUserId,
                filter.getRating(),
                filter.getFeedbackType(),
                filter.getFeedbackStatus(),
                filter.getQuery(),
                pageable
        );

        Meta<?> meta = Meta.builder()
                .pagination(Pagination.builder()
                        .count(pageResult.getNumberOfElements())
                        .perPage(size)
                        .currentPage(page)
                        .totalPages(pageResult.getTotalPages())
                        .total(pageResult.getTotalElements())
                        .build())
                .build();

        return PaginatedResponse.<FeedbackSelfResponse>builder()
                .data(pageResult.getContent())
                .meta(meta)
                .build();
    }


    @Override
    public PaginatedResponse<FeedbackResponse> filterFeedbacksForManager(FeedbackFilter filter, int page, int size) {
        String userId = userService.getCurrentUser().getId();

        Pageable pageable = PageRequest.of(Math.max(0, page - 1), size, Sort.by(Sort.Direction.DESC, "updatedAt"));
        Page<FeedbackResponse> pageResult = feedbackRepository.findAllByFilter(
                userId,
                filter.getBuildingId(),
                filter.getFeedbackStatus(),
                filter.getRating(),
                filter.getFeedbackType(),
                filter.getQuery(),
                pageable
        );

        Meta<?> meta = Meta.builder()
                .pagination(Pagination.builder()
                        .count(pageResult.getNumberOfElements())
                        .currentPage(page)
                        .perPage(size)
                        .total(pageResult.getTotalElements())
                        .totalPages(pageResult.getTotalPages())
                        .build())
                .build();

        return PaginatedResponse.<FeedbackResponse>builder()
                .data(pageResult.getContent())
                .meta(meta)
                .build();
    }

    @Override
    @Transactional
    public FeedbackStatusUpdateResponse updateFeedbackStatus(FeedbackStatusUpdateRequest request) {
        User currentUser = userService.getCurrentUser();
        String userId = currentUser.getId();

        Feedback feedback = feedbackRepository.findById(request.getFeedbackId())
                .orElseThrow(() -> new AppException(ErrorCode.FEED_BACK_NOT_FOUND));

        // check: là quản lí thì mới được update
        if (!feedback.getRoom().getFloor().getBuilding().getUser().getId().equals(userId)) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        feedback.setFeedbackStatus(request.getFeedbackStatus());
        feedback.setUpdatedAt(Instant.now());
        feedbackRepository.save(feedback);

        FeedbackProcessHistory history = FeedbackProcessHistory.builder()
                .feedback(feedback)
                .user(currentUser)
                .note(request.getNote())
                .time(feedback.getUpdatedAt())
                .build();
        feedbackProcessHistoryRepository.save(history);

        return FeedbackStatusUpdateResponse.builder()
                .id(history.getId())
                .feedbackId(feedback.getId())
                .content(feedback.getContent())
                .feedbackStatus(feedback.getFeedbackStatus())
                .updatedBy(currentUser.getFullName())
                .note(request.getNote())
                .updatedAt(feedback.getUpdatedAt())
                .build();
    }

     String normalizeContent(String content) {
        if (content == null) return "";
        return content
                .trim()
                .toLowerCase()
                .replaceAll("\\p{Punct}", "") //  bỏ dấu câu
                .replaceAll("\\s+", " ");     // chuẩn hóa khoảng trắng
    }
}