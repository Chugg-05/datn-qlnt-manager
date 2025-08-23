package com.example.datn_qlnt_manager.service.implement;

import com.example.datn_qlnt_manager.common.Meta;
import com.example.datn_qlnt_manager.common.Pagination;
import com.example.datn_qlnt_manager.dto.PaginatedResponse;
import com.example.datn_qlnt_manager.dto.filter.PaymentHistoryFilter;
import com.example.datn_qlnt_manager.dto.response.paymenthistory.PaymentHistoryResponse;
import com.example.datn_qlnt_manager.entity.PaymentHistory;
import com.example.datn_qlnt_manager.mapper.PaymentHistoryMapper;
import com.example.datn_qlnt_manager.repository.PaymentHistoryRepository;
import com.example.datn_qlnt_manager.service.PaymentHistoryService;
import com.example.datn_qlnt_manager.service.UserService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentHistoryServiceImpl implements PaymentHistoryService {

     PaymentHistoryRepository paymentHistoryRepository;
     PaymentHistoryMapper paymentHistoryMapper;
     UserService userService;

    @Override
    public PaginatedResponse<PaymentHistoryResponse> filterPaymentHistoriesByUserId(
            PaymentHistoryFilter filter, int page, int size) {

        var user = userService.getCurrentUser();
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "time"));

        Page<PaymentHistory> historyPage = paymentHistoryRepository.filterPaymentHistories(
                user.getId(),
                filter.getQuery(),
                filter.getPaymentStatus(),
                filter.getPaymentMethod(),
                filter.getFromAmount(),
                filter.getToAmount(),
                filter.getFromDate(),
                filter.getToDate(),
                pageable
        );

        return buildPaginatedResponse(historyPage, page, size);
    }

    @Override
    public PaginatedResponse<PaymentHistoryResponse> filterMyPaymentHistories(
            PaymentHistoryFilter filter, int page, int size) {

        var user = userService.getCurrentUser();
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "time"));

        Page<PaymentHistory> historyPage = paymentHistoryRepository.filterPaymentHistoriesByTenant(
                user.getId(),
                filter.getQuery(),
                filter.getPaymentStatus(),
                filter.getPaymentMethod(),
                filter.getFromAmount(),
                filter.getToAmount(),
                filter.getFromDate(),
                filter.getToDate(),
                pageable
        );

        return buildPaginatedResponse(historyPage, page, size);
    }

    private PaginatedResponse<PaymentHistoryResponse> buildPaginatedResponse(
            Page<PaymentHistory> historyPage, int page, int size) {

        List<PaymentHistoryResponse> responses = historyPage.getContent()
                .stream()
                .map(paymentHistoryMapper::toResponse)
                .toList();

        Pagination pagination = Pagination.builder()
                .total(historyPage.getTotalElements())
                .count(historyPage.getNumberOfElements())
                .perPage(size)
                .currentPage(page)
                .totalPages(historyPage.getTotalPages())
                .build();

        Meta<?> meta = Meta.builder().pagination(pagination).build();

        return PaginatedResponse.<PaymentHistoryResponse>builder()
                .data(responses)
                .meta(meta)
                .build();
    }
}
