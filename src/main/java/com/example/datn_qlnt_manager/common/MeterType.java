package com.example.datn_qlnt_manager.common;

import lombok.Getter;

@Getter
public enum MeterType {
    DIEN("DIEN"),
    NUOC("NUOC");

    private final String value;

    MeterType(String value) {
        this.value = value;
    }
}
