package com.example.datn_qlnt_manager.common;

import lombok.Getter;

@Getter
public enum ServiceStatus {
    HOAT_DONG("HOAT_DONG"),
    TAM_KHOA("TAM_KHOA"),
    KHONG_SU_DUNG("KHONG_SU_DUNG"),
    ;

    private final String value;

    ServiceStatus(String value) {
        this.value = value;
    }
}
