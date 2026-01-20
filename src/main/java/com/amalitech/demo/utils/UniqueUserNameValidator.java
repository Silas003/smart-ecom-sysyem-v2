package com.amalitech.demo.utils;

import com.amalitech.demo.repository.UserRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
//
//
//@Component
//public class UniqueUserNameValidator implements ConstraintValidator<UniqueUserName, String> {
//
//    private final UserRepository userRepository;
//
//    public UniqueUserNameValidator(UserRepository userRepository) {
//        this.userRepository = userRepository;
//    }
//
//    @Override
//    public boolean isValid(String username, ConstraintValidatorContext context) {
//        if (username == null || username.trim().isEmpty()) {
//            return true; // let @NotBlank handle empties
//        }
//        return !userRepository.existsByUsername(username);
//    }
//}

