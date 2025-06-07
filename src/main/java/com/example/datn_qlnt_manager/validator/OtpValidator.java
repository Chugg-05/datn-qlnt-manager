package com.example.datn_qlnt_manager.validator;

import java.util.regex.Pattern;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import com.example.datn_qlnt_manager.exception.ErrorCode;
import com.example.datn_qlnt_manager.validator.constraints.OtpConstraints;

public class OtpValidator implements ConstraintValidator<OtpConstraints, String> {
    // Chỉ gồm 1 đến 6 chữ số, không chứa khoảng trắng
    private static final Pattern OTP_PATTERN = Pattern.compile("^\\d{1,6}$");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            buildViolation(context, ErrorCode.INVALID_OTP_BLANK.name());
            return false;
        }

        boolean isValid = OTP_PATTERN.matcher(value).matches();

        if (!isValid) {
            buildViolation(context, ErrorCode.INVALID_OTP_FORMAT.name());
            return false;
        }

        return true;
    }

    @Override
    public void initialize(OtpConstraints constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    private void buildViolation(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
    }
}
