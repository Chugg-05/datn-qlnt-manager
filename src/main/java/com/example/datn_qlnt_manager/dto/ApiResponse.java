package com.example.datn_qlnt_manager.dto;

import com.example.datn_qlnt_manager.common.Meta;
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
public class ApiResponse<T> { // chuẩn hóa kiểu trả về của API
    @Builder.Default // dữ nguyên giá trị không cho ghi đè
    int code = 200;

    String message;
    T data;
    Meta meta;
}
