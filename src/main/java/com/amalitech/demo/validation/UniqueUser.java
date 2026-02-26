package com.amalitech.demo.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = com.amalitech.demo.validation.UniqueUserValidator.class)
@Target({ TYPE })
@Retention(RUNTIME)
public @interface UniqueUser {
    String message() default "User with given email or username already exists";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
