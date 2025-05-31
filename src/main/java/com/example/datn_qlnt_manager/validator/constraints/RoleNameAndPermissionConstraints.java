package com.example.datn_qlnt_manager.validator.constraints;

import java.lang.annotation.*;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import com.example.datn_qlnt_manager.validator.RoleNameAndPermissionValidator;

@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {RoleNameAndPermissionValidator.class})
public @interface RoleNameAndPermissionConstraints {
    String message() default "Invalid name";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
