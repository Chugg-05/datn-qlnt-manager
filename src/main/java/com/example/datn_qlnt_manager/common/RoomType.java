package com.example.datn_qlnt_manager.common;

import lombok.Getter;

@Getter
public enum RoomType {
    DON("DON"),
    GHEP("GHEP"),
    CAO_CAP("CAO_CAP"),
    KHAC("KHAC"),
    ;

    private final String value;

    RoomType(String value) {
        this.value = value;
    }
}
