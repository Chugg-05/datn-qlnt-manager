package com.example.datn_qlnt_manager.common;

import lombok.Getter;

@Getter
public enum RoomStatus {
    TRONG("trong"),
    DANG_THUE("dang_thue"),
    DA_DAT_COC("da_dat_coc"),
    DANG_BAO_TRI("dang_bao_tri"),
    CHUA_HOAN_THIEN("chua_hoan_thien"),
    TAM_KHOA("tam_khoa"),
    HUY_HOAT_DONG("huy_hoat_dong");

    private final String value;

    RoomStatus(String value) {
        this.value = value;
    }
}
