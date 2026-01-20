//package com.amalitech.demo.utils;
//
//import com.amalitech.demo.repository.UserRepository;
//import jakarta.validation.ConstraintValidator;
//import jakarta.validation.ConstraintValidatorContext;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//
//public class UniqueEmailValidator implements ConstraintValidator<UniqueEmail, String> {
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Override
//    public boolean isValid(String email, ConstraintValidatorContext context) {
//        // Allow null values — use @NotNull separately if needed
//        if (email == null || email.trim().isEmpty()) {
//            return true;
//        }
//        return !userRepository.existsByEmail(email);
//    }
//}
