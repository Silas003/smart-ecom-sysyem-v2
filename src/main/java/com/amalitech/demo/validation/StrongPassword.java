package com.amalitech.demo.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = com.amalitech.demo.validation.PasswordConstraintValidator.class)
@Target({ FIELD })
@Retention(RUNTIME)
public @interface StrongPassword {
    String message() default "Password must be at least 8 characters including uppercase, lowercase, number, and special character";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
