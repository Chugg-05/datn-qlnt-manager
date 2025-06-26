package com.example.datn_qlnt_manager.validator;

import java.util.regex.Pattern;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import com.example.datn_qlnt_manager.exception.ErrorCode;
import com.example.datn_qlnt_manager.validator.constraints.IdentityCardNumberConstraints;

public class IdentityCardNumberValidator implements ConstraintValidator<IdentityCardNumberConstraints, String> {

    private static final Pattern IDENTITY_CARD_NUMBER_REGEX = Pattern.compile("^\\d{12}$");

    @Override
    public boolean isValid(String value, jakarta.validation.ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            buildViolation(context, ErrorCode.INVALID_ID_NUMBER_BLANK.name());
            return false;
        }

        boolean isValidIdNumber = IDENTITY_CARD_NUMBER_REGEX.matcher(value).matches();

        if (!isValidIdNumber) {
            buildViolation(context, ErrorCode.INVALID_ID_NUMBER_FORMAT.name());
            return false;
        }

        return true;
    }

    @Override
    public void initialize(IdentityCardNumberConstraints constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    private void buildViolation(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
    }
}
