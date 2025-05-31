package com.example.datn_qlnt_manager.validator;

import java.util.regex.Pattern;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import com.example.datn_qlnt_manager.exception.ErrorCode;
import com.example.datn_qlnt_manager.validator.constraints.EmailConstraints;

public class EmailValidator implements ConstraintValidator<EmailConstraints, String> {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$");
    // Tên người dùng gồm chữ, số, các dấu '+, _, ., -', phải có '@' ngăn cách, tên miền gồm chữ, số, '. or -', dấu '.'
    // tước đuôi, đuôi miền từ 2 đến 6 chữ cái

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            buildViolation(context, ErrorCode.INVALID_EMAIL_BLANK.name());

            return false;
        }

        boolean isEmail = EMAIL_PATTERN.matcher(value).matches();

        if (!isEmail) {
            buildViolation(context, ErrorCode.INVALID_EMAIL_FORMAT.name());

            return false;
        }

        return true;
    }

    private void buildViolation(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
    }

    @Override
    public void initialize(EmailConstraints constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }
}
