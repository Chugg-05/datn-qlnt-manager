package com.example.datn_qlnt_manager.service.implement;

import com.example.datn_qlnt_manager.common.Meta;
import com.example.datn_qlnt_manager.common.Pagination;
import com.example.datn_qlnt_manager.dto.PaginatedResponse;
import com.example.datn_qlnt_manager.dto.filter.ServicePriceHistoryFilter;
import com.example.datn_qlnt_manager.dto.response.servicePriceHistory.ServicePriceHistoryResponse;
import com.example.datn_qlnt_manager.entity.ServicePriceHistory;
import com.example.datn_qlnt_manager.mapper.ServicePriceHistoryMapper;
import com.example.datn_qlnt_manager.repository.ServicePriceHistoryRepository;
import com.example.datn_qlnt_manager.service.ServicePriceHistoryService;
import com.example.datn_qlnt_manager.service.UserService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ServicePriceHistoryServiceImpl implements ServicePriceHistoryService {

     ServicePriceHistoryRepository servicePriceHistoryRepository;
     ServicePriceHistoryMapper servicePriceHistoryMapper;
     UserService userService;

    @Override
    public PaginatedResponse<ServicePriceHistoryResponse> getServicePriceHistories(
            int page, int size, ServicePriceHistoryFilter filter) {

        String userId = userService.getCurrentUser().getId();
        Pageable pageable = PageRequest.of(Math.max(0, page - 1), size);

        LocalDateTime start = filter.getStartDate() != null ? filter.getStartDate().atStartOfDay() : null;
        LocalDateTime end = filter.getEndDate() != null ? filter.getEndDate().atTime(LocalTime.MAX) : null;

        Page<ServicePriceHistory> result = servicePriceHistoryRepository.filterByUser(
                userId,
                filter.getServiceName(),
                filter.getMinOldPrice(),
                filter.getMaxOldPrice(),
                filter.getMinNewPrice(),
                filter.getMaxNewPrice(),
                start,
                end,
                pageable
        );

        List<ServicePriceHistoryResponse> responseList = result.getContent().stream()
                .map(servicePriceHistoryMapper::toResponse)
                .toList();

        Meta<?> meta = Meta.builder()
                .pagination(Pagination.builder()
                        .count(result.getNumberOfElements())
                        .perPage(size)
                        .currentPage(page)
                        .totalPages(result.getTotalPages())
                        .total(result.getTotalElements())
                        .build())
                .build();

        return PaginatedResponse.<ServicePriceHistoryResponse>builder()
                .data(responseList)
                .meta(meta)
                .build();
    }
}
