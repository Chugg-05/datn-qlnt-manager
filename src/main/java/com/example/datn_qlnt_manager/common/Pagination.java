package com.example.datn_qlnt_manager.common;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Pagination {
    Long total;
    Integer count;
    Integer perPage;
    Integer currentPage;
    Integer totalPages;
}
