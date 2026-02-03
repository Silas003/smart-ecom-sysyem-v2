package com.amalitech.demo.exceptions;


import jakarta.validation.ConstraintViolationException;
import org.springframework.aop.AopInvocationException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ExceptionHandlers {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<?> handleMethodArgumentTypeMismatch(MethodArgumentNotValidException ex, WebRequest request) {
        Map<String,Object> errors= new HashMap<>();
        errors.put("timestamp",LocalDateTime.now());
        errors.put("message","Validation Failed");
        errors.put("details",ex.getBindingResult().getFieldErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).toList());
        errors.put("path",request.getDescription(false));
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<?> handleEntityNotFoundException(EntityNotFoundException ex,WebRequest request) {
        Map<String,Object> errors= new HashMap<>();
        errors.put("timestamp",LocalDateTime.now());
        errors.put("message",ex.getMessage());
        errors.put("path",request.getDescription(false));
        return new ResponseEntity<>(errors, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String,Object>> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex, WebRequest request) {
        Map<String,Object> errors= new HashMap<>();
        errors.put("timestamp", LocalDateTime.now());
        errors.put("message","Invalid parameter: " + ex.getName());
        errors.put("path",request.getDescription(false));
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String,Object>> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
        Map<String,Object> errors= new HashMap<>();
        errors.put("timestamp", LocalDateTime.now());
        errors.put("message","Invalid parameter: " + ex.getMessage());
        errors.put("path",request.getDescription(false));
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Map<String,Object>> handleNoResourceFoundException(NoResourceFoundException ex){
        Map<String,Object> errors = new HashMap<String,Object>();
        errors.put("timestamp",LocalDateTime.now());
        errors.put("message","Resource Not Found");
        errors.put("details",ex.getMessage());
        return new ResponseEntity<>(errors,HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String,Object>> handleConstraintViolationException(ConstraintViolationException ex){
        Map<String,Object> errors = new HashMap<String,Object>();
        errors.put("timestamp",LocalDateTime.now());
        errors.put("message","Validations failed");
        errors.put("details",ex.getMessage());
        return new ResponseEntity<>(errors,HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AopInvocationException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Map<String,Object>>  handleAopInvocationException(AopInvocationException ex){
        Map<String,Object> errors = new HashMap<String,Object>();
        errors.put("timestamp",LocalDateTime.now());
        errors.put("message","Internal Server Error");
        errors.put("details",ex.getMessage());
        return new ResponseEntity<>(errors,HttpStatus.INTERNAL_SERVER_ERROR);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAllExceptions(Exception ex){
        return new ResponseEntity<>(Map.of("error", "internal server error"), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
