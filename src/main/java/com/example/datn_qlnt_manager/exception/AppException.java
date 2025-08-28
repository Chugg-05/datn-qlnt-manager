package com.example.datn_qlnt_manager.exception;

import com.example.datn_qlnt_manager.configuration.Translator;
import lombok.Getter;

@Getter
public class AppException extends RuntimeException {
    private final ErrorCode errorCode;

    public AppException(ErrorCode errorCode) {
        super(Translator.toLocale(errorCode.getMessage()));
        this.errorCode = errorCode;
    }
}
