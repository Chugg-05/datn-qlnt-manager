package com.example.datn_qlnt_manager.common;

import lombok.Getter;

@Getter
public enum RoomType {
    DON("don"),
    GHEP("ghep"),
    CAO_CAP("cao_cap"),
    KHAC("khac"),
    ;

    private final String value;

    RoomType(String value) {
        this.value = value;
    }
}
