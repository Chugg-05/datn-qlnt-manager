package com.example.datn_qlnt_manager.common;

import lombok.Getter;

@Getter
public enum RoomStatus {
    TRONG("TRONG"),
    DANG_THUE("DANG_THUE"),
    DA_DAT_COC("DA_DAT_COC"),
    DANG_BAO_TRI("DANG_BAO_TRI");

    private final String value;

    RoomStatus(String value) {
        this.value = value;
    }
}
