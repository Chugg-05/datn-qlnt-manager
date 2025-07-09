package com.example.datn_qlnt_manager.common;

import lombok.Getter;

@Getter
public enum ServiceType {
    CO_DINH("CO_DINH"),
    TINH_THEO_SO("TINH_THEO_SO"),
    TIEN_PHONG("TIEN_PHONG"),
    ;

    private final String value;

    ServiceType(String value) {
        this.value = value;
    }
}
