package com.example.datn_qlnt_manager.validator;

import java.util.regex.Pattern;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import com.example.datn_qlnt_manager.exception.ErrorCode;
import com.example.datn_qlnt_manager.validator.constraints.PhoneNumberConstraints;

public class PhoneNumberValidator implements ConstraintValidator<PhoneNumberConstraints, String> {

    private static final Pattern PHONE_NUMBER_PATTERN = Pattern.compile("^(\\\\+84|0)(3|5|7|8|9)[0-9]{8}$");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            buildViolation(context, ErrorCode.INVALID_PHONE_BLANK.name());

            return false;
        }

        String normalizedPhoneNumber = value.replaceAll("[^0-9+]", ""); // Loại bỏ khoảng trắng
        boolean isPhoneNumber =
                PHONE_NUMBER_PATTERN.matcher(normalizedPhoneNumber).matches();

        if (!isPhoneNumber) {
            buildViolation(context, ErrorCode.INVALID_PHONE_NUMBER_FORMAT.name());

            return false;
        }

        return true;
    }

    @Override
    public void initialize(PhoneNumberConstraints constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    private void buildViolation(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
    }
}
