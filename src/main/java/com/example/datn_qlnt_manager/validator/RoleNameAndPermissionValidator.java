package com.example.datn_qlnt_manager.validator;

import java.util.regex.Pattern;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import com.example.datn_qlnt_manager.exception.ErrorCode;
import com.example.datn_qlnt_manager.validator.constraints.RoleNameAndPermissionConstraints;

public class RoleNameAndPermissionValidator implements ConstraintValidator<RoleNameAndPermissionConstraints, String> {
    private static final Pattern ROLE_NAME_AND_PERMISSION_PATTERN = Pattern.compile("^[A-Z0-9_]+$");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            buildViolation(context, ErrorCode.INVALID_ROLE_NAME_AND_PERMISSION_BLANK.name());
            return false;
        }

        boolean isValid = ROLE_NAME_AND_PERMISSION_PATTERN.matcher(value).matches();

        if (!isValid) {
            buildViolation(context, ErrorCode.INVALID_ROLE_NAME_AND_PERMISSION_FORMAT.name());
            return false;
        }

        return true;
    }

    @Override
    public void initialize(RoleNameAndPermissionConstraints constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    private void buildViolation(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
    }
}
