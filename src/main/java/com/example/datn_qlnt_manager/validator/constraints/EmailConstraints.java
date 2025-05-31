package com.example.datn_qlnt_manager.validator.constraints;

import java.lang.annotation.*;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import com.example.datn_qlnt_manager.validator.EmailValidator;

@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {EmailValidator.class})
public @interface EmailConstraints {
    String message() default "Invalid Username or email";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
