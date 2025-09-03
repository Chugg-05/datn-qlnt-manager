package com.example.datn_qlnt_manager.common;

public enum RepresentativeChangeType {
    NGUOI_CUNG_PHONG("Đổi cho người cùng phòng"),
    NGUOI_NGOAI("Nhượng cho người ngoài");

    private final String description;

    RepresentativeChangeType(String description) {
        this.description = description;
    }
}
