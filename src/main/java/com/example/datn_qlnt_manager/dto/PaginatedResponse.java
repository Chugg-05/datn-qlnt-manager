package com.example.datn_qlnt_manager.dto;

import java.util.List;

import com.example.datn_qlnt_manager.common.Meta;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaginatedResponse<T> {
    List<T> data;
    Meta<?> meta;
}
