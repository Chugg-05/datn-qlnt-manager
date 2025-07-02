package com.example.datn_qlnt_manager.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import com.example.datn_qlnt_manager.exception.ErrorCode;
import com.example.datn_qlnt_manager.validator.constraints.PasswordConstraints;

public class PasswordValidator implements ConstraintValidator<PasswordConstraints, String> {
    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null || password.isBlank()) {
            buildViolation(context, ErrorCode.INVALID_PASSWORD.name());

            return false;
        }

        if (password.length() < 6) {
            buildViolation(context, ErrorCode.INVALID_PASSWORD_LENGTH.name());

            return false;
        }

        if (!password.matches(".*[A-Z].*")) {
            buildViolation(context, ErrorCode.INVALID_PASSWORD_UPPERCASE.name());

            return false;
        }

        if (!password.matches(".*[a-z].*")) {
            buildViolation(context, ErrorCode.INVALID_PASSWORD_LOWERCASE.name());

            return false;
        }

        if (!password.matches(".*\\d.*")) {
            buildViolation(context, ErrorCode.INVALID_PASSWORD_NUMBER.name());

            return false;
        }

        if (!password.matches(".*[@#$%&*!].*")) {
            buildViolation(context, ErrorCode.INVALID_PASSWORD_SPECIAL_CHARACTERS.name());

            return false;
        }
        return true;
    }

    private void buildViolation(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
    }

    @Override
    public void initialize(PasswordConstraints constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }
}
