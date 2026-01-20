//package com.amalitech.demo.utils;
//
//import com.amalitech.demo.repository.CategoryRepository;
//import jakarta.validation.ConstraintValidator;
//import jakarta.validation.ConstraintValidatorContext;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//
//public class UniqueCategoryValidator implements ConstraintValidator<UniqueCategoryName, String> {
//
//    @Autowired
//    private  CategoryRepository categoryRepository;
//
//    @Override
//    public boolean isValid(String value, ConstraintValidatorContext context) {
//        // Skip validation if null or blank — let @NotBlank handle it
//        if (value == null || value.trim().isEmpty()) {
//            return true;
//        }
//        return !categoryRepository.existsByName(value);
//    }
//}
//
