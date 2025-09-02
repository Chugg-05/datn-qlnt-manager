package com.example.datn_qlnt_manager.service.implement;

import java.time.Instant;
import java.util.List;

import com.example.datn_qlnt_manager.common.ContractStatus;
import com.example.datn_qlnt_manager.common.Meta;
import com.example.datn_qlnt_manager.common.Pagination;
import com.example.datn_qlnt_manager.dto.PaginatedResponse;
import com.example.datn_qlnt_manager.dto.filter.FeedBackSelfFilter;
import com.example.datn_qlnt_manager.dto.filter.FeedbackFilter;
import com.example.datn_qlnt_manager.dto.request.feedback.RejectFeedbackRequest;
import com.example.datn_qlnt_manager.repository.*;
import jakarta.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.example.datn_qlnt_manager.common.FeedbackStatus;
import com.example.datn_qlnt_manager.dto.request.feedback.FeedbackCreationRequest;
import com.example.datn_qlnt_manager.dto.request.feedback.FeedbackStatusUpdateRequest;
import com.example.datn_qlnt_manager.dto.request.feedback.FeedbackUpdateRequest;
import com.example.datn_qlnt_manager.dto.response.feedback.FeedbackResponse;
import com.example.datn_qlnt_manager.entity.*;
import com.example.datn_qlnt_manager.exception.AppException;
import com.example.datn_qlnt_manager.exception.ErrorCode;
import com.example.datn_qlnt_manager.mapper.FeedbackMapper;
import com.example.datn_qlnt_manager.service.FeedbackService;
import com.example.datn_qlnt_manager.service.UserService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FeedbackServiceImpl implements FeedbackService {

    FeedbackRepository feedbackRepository;
    TenantRepository tenantRepository;
    ContractRepository contractRepository;
    RoomRepository roomRepository;
    FeedbackMapper feedbackMapper;
    UserService userService;
    FeedbackProcessHistoryRepository feedbackProcessHistoryRepository;

    @Override
    @Transactional
    public FeedbackResponse createFeedback(FeedbackCreationRequest request) {
        User currentUser = userService.getCurrentUser();

        Tenant tenant = tenantRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new AppException(ErrorCode.TENANT_NOT_FOUND));

        List<Contract> contracts = contractRepository.findByTenantIdAndStatusIn(
                tenant.getId(),
                List.of(ContractStatus.HIEU_LUC, ContractStatus.SAP_HET_HAN)
        );

        if (contracts.isEmpty()) {
            throw new AppException(ErrorCode.TENANT_NOT_IN_CONTRACT);
        }

            Contract  contract = contracts.stream()
                    .filter(c -> c.getRoom().getId().equals(request.getRoomId()))
                    .findFirst()
                    .orElseThrow(() -> new AppException(ErrorCode.TENANT_NOT_RENT_ROOM));

        Room room = contract.getRoom();
        if (room == null) {
            throw new AppException(ErrorCode.ROOM_NOT_FOUND);
        }

        String normalizedContent = normalizeContent(request.getContent());
        List<Feedback> existingFeedbacks =
                feedbackRepository.findAllByNameSenderAndRoomCodeAndFeedbackTypeAndFeedbackStatusIn(
                        currentUser.getFullName(),
                        room.getRoomCode(),
                        request.getFeedbackType(),
                        List.of(FeedbackStatus.CHUA_XU_LY)
                );

        boolean isDuplicate = existingFeedbacks.stream()
                .anyMatch(f -> f.getContent() != null &&
                        normalizeContent(f.getContent()).equals(normalizedContent));

        if (isDuplicate) {
            throw new AppException(ErrorCode.FEED_BACK_DUPLICATED);
        }

        Feedback feedback = feedbackMapper.toEntity(request);
        feedback.setNameSender(currentUser.getFullName());
        feedback.setRoomCode(room.getRoomCode());
        feedback.setCreatedAt(Instant.now());
        feedback.setUpdatedAt(Instant.now());

        Feedback savedFeedback = feedbackRepository.save(feedback);

        FeedbackProcessHistory history = FeedbackProcessHistory.builder()
                .feedback(savedFeedback)
                .user(currentUser)
                .note("Khách thuê gửi phản hồi")
                .time(savedFeedback.getUpdatedAt())
                .build();
        feedbackProcessHistoryRepository.save(history);

        return feedbackMapper.toResponse(savedFeedback);
    }

    @Override
    public FeedbackResponse updateFeedback(String feedbackId, FeedbackUpdateRequest request) {
        Feedback feedback = feedbackRepository
                .findById(feedbackId)
                .orElseThrow(() -> new AppException(ErrorCode.FEED_BACK_NOT_FOUND));

        User currentUser = userService.getCurrentUser();
        if (!feedback.getNameSender().equals(currentUser.getFullName())) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        feedbackMapper.updateFeedback(feedback, request);
        feedback.setUpdatedAt(Instant.now());

        return feedbackMapper.toResponse(feedbackRepository.save(feedback));
    }

    @Override
    public PaginatedResponse<FeedbackResponse> filterMyFeedbacks(FeedBackSelfFilter filter, int page, int size) {
        String currentUserName = userService.getCurrentUser().getFullName(); // hoặc userId nếu dùng id

        Pageable pageable = PageRequest.of(Math.max(0, page - 1), size, Sort.by(Sort.Direction.DESC, "updatedAt"));

        Page<Feedback> pageResult = feedbackRepository.findAllBySenderWithFilter(
                currentUserName,
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

        List<FeedbackResponse> responses = pageResult.stream()
                .map(feedbackMapper::toResponse)
                .toList();

        return PaginatedResponse.<FeedbackResponse>builder()
                .data(responses)
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
                pageable);

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
public FeedbackResponse updateFeedbackStatus(FeedbackStatusUpdateRequest request) {
    User currentUser = userService.getCurrentUser();

    Feedback feedback = feedbackRepository.findById(request.getFeedbackId())
            .orElseThrow(() -> new AppException(ErrorCode.FEED_BACK_NOT_FOUND));

    checkOwnerPermission(feedback, currentUser.getId());

    if (feedback.getFeedbackStatus() == FeedbackStatus.DA_XU_LY ||
            feedback.getFeedbackStatus() == FeedbackStatus.TU_CHOI) {
        throw new AppException(ErrorCode.CANNOT_UPDATE_PROCESSED_FEEDBACK);
    }

    feedback.setFeedbackStatus(request.getFeedbackStatus());
    feedback.setUpdatedAt(Instant.now());
    Feedback savedFeedback = feedbackRepository.save(feedback);

    FeedbackProcessHistory history = getFeedbackHistory(savedFeedback);
    history.setNote(request.getNote());
    history.setTime(savedFeedback.getUpdatedAt());
    feedbackProcessHistoryRepository.save(history);

    return feedbackMapper.toResponse(savedFeedback);
}

    @Override
    @Transactional
    public FeedbackResponse rejectFeedback(String feedbackId, RejectFeedbackRequest request) {
        User currentUser = userService.getCurrentUser();

        Feedback feedback = feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new AppException(ErrorCode.FEED_BACK_NOT_FOUND));

        if (feedback.getFeedbackStatus() != FeedbackStatus.CHUA_XU_LY) {
            throw new AppException(ErrorCode.INVALID_FEEDBACK_STATUS);
        }

        checkOwnerPermission(feedback, currentUser.getId());

        feedback.setFeedbackStatus(FeedbackStatus.TU_CHOI);
        feedback.setRejectionReason(request.getRejectionReason());
        feedback.setUpdatedAt(Instant.now());
        Feedback savedFeedback = feedbackRepository.save(feedback);

        FeedbackProcessHistory history = getFeedbackHistory(savedFeedback);
        history.setNote("Chủ building từ chối feedback");
        history.setTime(savedFeedback.getUpdatedAt());
        feedbackProcessHistoryRepository.save(history);

        return feedbackMapper.toResponse(savedFeedback);
    }

    String normalizeContent(String content) {
        if (content == null) return "";
        return content.trim()
                .toLowerCase()
                .replaceAll("\\p{Punct}", "")
                .replaceAll("\\s+", " ");
    }

    private void checkOwnerPermission(Feedback feedback, String userId) {
        List<Room> rooms = roomRepository.findAllByRoomCode(feedback.getRoomCode());
        if (rooms.isEmpty()) {
            throw new AppException(ErrorCode.ROOM_NOT_FOUND);
        }

        boolean isOwner = rooms.stream()
                .anyMatch(r -> r.getFloor().getBuilding().getUser().getId().equals(userId));

        if (!isOwner) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
    }

    private FeedbackProcessHistory getFeedbackHistory(Feedback feedback) {
        return feedbackProcessHistoryRepository.findAllByFeedbackId(feedback.getId())
                .orElseThrow(() -> new AppException(ErrorCode.FEED_BACK_HISTORY_NOT_FOUND));
    }

}
