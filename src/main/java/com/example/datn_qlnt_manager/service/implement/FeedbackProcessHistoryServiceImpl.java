package com.example.datn_qlnt_manager.service.implement;

import static lombok.AccessLevel.PRIVATE;

import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import com.example.datn_qlnt_manager.common.Meta;
import com.example.datn_qlnt_manager.common.Pagination;
import com.example.datn_qlnt_manager.dto.PaginatedResponse;
import com.example.datn_qlnt_manager.dto.response.feedbackProcessHistory.FeedbackProcessHistoryResponse;
import com.example.datn_qlnt_manager.repository.FeedbackProcessHistoryRepository;
import com.example.datn_qlnt_manager.service.FeedbackProcessHistoryService;
import com.example.datn_qlnt_manager.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class FeedbackProcessHistoryServiceImpl implements FeedbackProcessHistoryService {

    FeedbackProcessHistoryRepository feedbackProcessHistoryRepository;
    UserService userService;

    @Override
    public PaginatedResponse<FeedbackProcessHistoryResponse> getAllByUserId(
            String feedbackId, String query, int page, int size) {
        String userId = userService.getCurrentUser().getId();

        Pageable pageable = PageRequest.of(Math.max(0, page - 1), size, Sort.by(Sort.Direction.DESC, "time"));

        Page<FeedbackProcessHistoryResponse> resultPage =
                feedbackProcessHistoryRepository.findAllByCurrentUser(userId, feedbackId, query, pageable);

        Meta<?> meta = Meta.builder()
                .pagination(Pagination.builder()
                        .count(resultPage.getNumberOfElements())
                        .currentPage(page)
                        .perPage(size)
                        .total(resultPage.getTotalElements())
                        .totalPages(resultPage.getTotalPages())
                        .build())
                .build();

        return PaginatedResponse.<FeedbackProcessHistoryResponse>builder()
                .data(resultPage.getContent())
                .meta(meta)
                .build();
    }
}
