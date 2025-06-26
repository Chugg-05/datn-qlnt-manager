package com.example.datn_qlnt_manager.exception;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import jakarta.validation.ConstraintViolation;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.example.datn_qlnt_manager.dto.ApiResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    // xử lý ngoại lệ chung
    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ApiResponse<?>> handleException(Exception e) {
        log.error("Unhandled exception occurred: {}", e.getMessage(), e);
        ApiResponse<?> apiResponse = ApiResponse.builder()
                .message(ErrorCode.INTERNAL_SERVER_ERROR.getMessage())
                .code(ErrorCode.INTERNAL_SERVER_ERROR.getCode())
                .build();

        return ResponseEntity.badRequest().body(apiResponse);
    }

    // xử lý ngoại lệ tùy chỉnh
    @ExceptionHandler(value = AppException.class)
    public ResponseEntity<ApiResponse<?>> handleAppException(AppException e) {
        ErrorCode errorCode = e.getErrorCode();
        ApiResponse<?> apiResponse = ApiResponse.builder()
                .message(errorCode.getMessage())
                .code(errorCode.getCode())
                .build();

        return ResponseEntity.badRequest().body(apiResponse);
    }

    // trình xử lý không tìm thấy tài nguyên
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<?> handleNotFoundException(NoResourceFoundException e) {
        log.warn("No resource found: {}", e.getMessage(), e);

        ErrorCode errorCode = ErrorCode.API_ENDPOINT_NOT_FOUND;
        ApiResponse<?> apiResponse = ApiResponse.builder()
                .message(errorCode.getMessage())
                .code(errorCode.getCode())
                .build();

        return ResponseEntity.badRequest().body(apiResponse);
    }

    // trình xử lý ngoại lệ khóa tài khoản
    @ExceptionHandler(value = LockedException.class)
    public ResponseEntity<ApiResponse<?>> handleLockedException(LockedException e) {
        log.warn("Account is locked: {}", e.getMessage(), e);

        ErrorCode errorCode = ErrorCode.ACCOUNT_HAS_BEEN_LOCKED;
        ApiResponse<?> apiResponse = ApiResponse.builder()
                .message(errorCode.getMessage())
                .code(errorCode.getCode())
                .build();

        return ResponseEntity.badRequest().body(apiResponse);
    }

    // trình xử lý ngoại lệ thiếu tiêu đề yêu cầu
    @ExceptionHandler(value = MissingRequestHeaderException.class)
    public ResponseEntity<ApiResponse<?>> handleMissingRequestHeaderException(MissingRequestHeaderException e) {
        ApiResponse<?> apiResponse = ApiResponse.builder()
                .message(e.getMessage())
                .code(e.getStatusCode().value())
                .build();

        return ResponseEntity.badRequest().body(apiResponse);
    }

    // trình xử lý ngoại lệ không hợp lệ
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String enumKey = e.getFieldError() != null ? e.getFieldError().getDefaultMessage() : "INVALID_KEY";

        ErrorCode errorCode = ErrorCode.INVALID_KEY;
        Map<String, Object> attributes = new HashMap<>();

        try {
            errorCode = ErrorCode.valueOf(enumKey);

            var allErrors = e.getBindingResult().getAllErrors();
            ConstraintViolation<?> constraintViolation = null;

            if (!allErrors.isEmpty()) {
                constraintViolation = allErrors.getFirst().unwrap(ConstraintViolation.class);
            }

            if (constraintViolation != null) {
                attributes = constraintViolation.getConstraintDescriptor().getAttributes();
            }
        } catch (IllegalArgumentException ex) {
            log.debug("Invalid enum key: {}", enumKey);
        }

        ApiResponse<?> apiResponse = new ApiResponse<>();

        apiResponse.setCode(errorCode.getCode());
        apiResponse.setMessage(
                Objects.nonNull(attributes)
                        ? mapAttribute(errorCode.getMessage(), attributes)
                        : errorCode.getMessage());

        return ResponseEntity.badRequest().body(apiResponse);
    }

    // trình xử lý ngoại lệ không hợp lệ
    private String mapAttribute(String message, Map<String, Object> attributes) {
        String result = message;
        for (Map.Entry<String, Object> entry : attributes.entrySet()) {
            result = result.replace("{" + entry.getKey() + "}", String.valueOf(entry.getValue()));
        }
        return result;
    }
}
