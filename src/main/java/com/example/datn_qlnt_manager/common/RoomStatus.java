package com.example.datn_qlnt_manager.common;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum RoomStatus {
    TRONG("TRONG"),
    DANG_THUE("DANG_THUE"),
    DA_DAT_COC("DA_DAT_COC"),
    DANG_BAO_TRI("DANG_BAO_TRI"),
    CHUA_HOAN_THIEN("CHUA_HOAN_THIEN"),
    TAM_KHOA("TAM_KHOA"),
    HUY_HOAT_DONG("HUY_HOAT_DONG");

    private final String value;

    RoomStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static RoomStatus fromValue(String value) {
        for (RoomStatus status : RoomStatus.values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid RoomStatus: " + value);
    }
}
