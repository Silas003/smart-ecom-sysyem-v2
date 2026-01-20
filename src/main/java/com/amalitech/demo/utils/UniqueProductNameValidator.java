//package com.amalitech.demo.utils;
//
//import com.amalitech.demo.repository.ProductRepository;
//import jakarta.validation.ConstraintValidator;
//import jakarta.validation.ConstraintValidatorContext;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//public class UniqueProductNameValidator implements ConstraintValidator<UniqueProductName, String> {
//
//    @Autowired
//    private  ProductRepository productRepository;
//
//
//
//    @Override
//    public boolean isValid(String value, ConstraintValidatorContext context) {
//        // Skip validation if null or blank — let @NotBlank handle it
//        if (value == null || value.trim().isEmpty()) {
//            return true;
//        }
//        return !productRepository.existsByName(value);
//    }
//}
