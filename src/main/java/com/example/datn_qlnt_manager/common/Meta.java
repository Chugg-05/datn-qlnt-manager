package com.example.datn_qlnt_manager.common;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Meta<T> {
    T tokenInfo;
    Pagination pagination;
}
