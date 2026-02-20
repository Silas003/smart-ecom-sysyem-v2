package com.amalitech.demo.validation;

import com.amalitech.demo.dto.request.UpdateUserRequest;
import com.amalitech.demo.dto.request.UserRequest;
import com.amalitech.demo.models.User;
import com.amalitech.demo.repository.UserRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Component
public class UniqueUserValidator implements ConstraintValidator<UniqueUser, Object> {

    @Autowired
    private UserRepository userRepository;

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) return true;

        String email = null;
        String username = null;
        Long id = null;

        if (value instanceof UserRequest) {
            UserRequest ur = (UserRequest) value;
            email = ur.getEmail();
            username = ur.getUsername();
        } else if (value instanceof UpdateUserRequest) {
            UpdateUserRequest ur = (UpdateUserRequest) value;
            id = ur.getId();
            email = ur.getEmail();
            username = ur.getUsername();
        } else {
            // try reflection fallback
            try {
                Method getEmail = value.getClass().getMethod("getEmail");
                Object e = getEmail.invoke(value);
                if (e != null) email = e.toString();
                Method getUsername = value.getClass().getMethod("getUsername");
                Object u = getUsername.invoke(value);
                if (u != null) username = u.toString();
                try {
                    Method getId = value.getClass().getMethod("getId");
                    Object i = getId.invoke(value);
                    if (i instanceof Number) id = ((Number) i).longValue();
                } catch (NoSuchMethodException ignored) {
                }
            } catch (ReflectiveOperationException ex) {
                return true;
            }
        }

        boolean valid = true;

        if (email != null) {
            User byEmail = userRepository.findByEmail(email).orElse(null);
            if (byEmail != null && (id == null || !byEmail.getId().equals(id))) {
                valid = false;
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("Email already in use").addPropertyNode("email").addConstraintViolation();
            }
        }

        if (username != null) {
            User byUsername = userRepository.findByUsername(username).orElse(null);
            if (byUsername != null && (id == null || !byUsername.getId().equals(id))) {
                valid = false;
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("Username already in use").addPropertyNode("username").addConstraintViolation();
            }
        }

        return valid;
    }
}
