package com.example.datn_qlnt_manager.common;

import lombok.Getter;

@Getter
public enum ServiceAppliedBy {
    PHONG("PHONG"),
    NGUOI("NGUOI"),
    TANG("TANG"),
    ;

    private final String value;

    ServiceAppliedBy(String value) {
        this.value = value;
    }
}
