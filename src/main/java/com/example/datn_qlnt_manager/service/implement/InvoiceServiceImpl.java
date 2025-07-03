package com.example.datn_qlnt_manager.service.implement;

import com.example.datn_qlnt_manager.common.Meta;
import com.example.datn_qlnt_manager.common.Pagination;
import com.example.datn_qlnt_manager.dto.PaginatedResponse;
import com.example.datn_qlnt_manager.dto.filter.InvoiceFilter;
import com.example.datn_qlnt_manager.dto.response.invoice.InvoiceResponse;
import com.example.datn_qlnt_manager.repository.InvoiceRepository;
import com.example.datn_qlnt_manager.service.InvoiceService;
import com.example.datn_qlnt_manager.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InvoiceServiceImpl implements InvoiceService {
    InvoiceRepository invoiceRepository;
    UserService userService;

    @Override
    public PaginatedResponse<InvoiceResponse> getPageAndSearchAndFilter(
            InvoiceFilter filter,
            int page,
            int size
    ) {
        Pageable pageable = PageRequest.of(Math.max(0, page - 1), size);
        var user = userService.getCurrentUser();

        Page<InvoiceResponse> paging = invoiceRepository.getPageAnsSearchAndFilterInvoiceByOwnerId(
                user.getId(),
                filter.getQuery(),
                filter.getBuilding(),
                filter.getFloor(),
                filter.getMonth(),
                filter.getYear(),
                filter.getMinGrantTotal(),
                filter.getMaxGrantTotal(),
                filter.getInvoiceStatus(),
                pageable
        );

        List<InvoiceResponse> invoiceResponses = paging.getContent().stream().toList();

        Meta<?> meta = Meta.builder()
                .pagination(Pagination.builder()
                        .count(paging.getNumberOfElements())
                        .perPage(size)
                        .currentPage(page)
                        .totalPages(paging.getTotalPages())
                        .total(paging.getTotalElements())
                        .build())
                .build();

        return PaginatedResponse.<InvoiceResponse>builder()
                .data(invoiceResponses)
                .meta(meta)
                .build();
     }

}
