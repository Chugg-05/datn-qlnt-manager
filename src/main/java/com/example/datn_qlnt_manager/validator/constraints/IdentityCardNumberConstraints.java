package com.example.datn_qlnt_manager.validator.constraints;

import java.lang.annotation.*;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import com.example.datn_qlnt_manager.validator.IdentityCardNumberValidator;

@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {IdentityCardNumberValidator.class})
public @interface IdentityCardNumberConstraints {
    String message() default "Invalid identity card number";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
