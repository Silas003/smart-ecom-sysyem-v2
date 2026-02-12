package com.amalitech.demo.exceptions;

import com.amalitech.demo.dto.ResponseDto;
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

    private ResponseEntity<ResponseDto<Object>> build(HttpStatus status, String message, Object details) {
        ResponseDto<Object> dto = new ResponseDto<>(status, message, details);
        return new ResponseEntity<>(dto, status);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ResponseDto<Object>> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, WebRequest request) {
        Map<String,Object> details= new HashMap<>();
        details.put("timestamp",LocalDateTime.now());
        details.put("errors",ex.getBindingResult().getFieldErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).toList());
        details.put("path",request.getDescription(false));
        return build(HttpStatus.BAD_REQUEST, "Validation Failed", details);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ResponseDto<Object>> handleEntityNotFoundException(EntityNotFoundException ex, WebRequest request) {
        Map<String,Object> details= new HashMap<>();
        details.put("timestamp",LocalDateTime.now());
        details.put("path",request.getDescription(false));
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), details);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ResponseDto<Object>> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex, WebRequest request) {
        Map<String,Object> details= new HashMap<>();
        details.put("timestamp", LocalDateTime.now());
        details.put("path",request.getDescription(false));
        details.put("parameter", ex.getName());
        details.put("requiredType", ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "");
        return build(HttpStatus.BAD_REQUEST, "Invalid parameter", details);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ResponseDto<Object>> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
        Map<String,Object> details= new HashMap<>();
        details.put("timestamp", LocalDateTime.now());
        details.put("path",request.getDescription(false));
        return build(HttpStatus.BAD_REQUEST, ex.getMessage() == null ? "Invalid parameter" : ex.getMessage(), details);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ResponseDto<Object>> handleNoResourceFoundException(NoResourceFoundException ex, WebRequest request){
        Map<String,Object> details = new HashMap<>();
        details.put("timestamp",LocalDateTime.now());
        details.put("details",ex.getMessage());
        details.put("path",request.getDescription(false));
        return build(HttpStatus.NOT_FOUND, "Resource Not Found", details);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ResponseDto<Object>> handleConstraintViolationException(ConstraintViolationException ex, WebRequest request){
        Map<String,Object> details = new HashMap<>();
        details.put("timestamp",LocalDateTime.now());
        details.put("details",ex.getMessage());
        details.put("path",request.getDescription(false));
        return build(HttpStatus.BAD_REQUEST, "Validations failed", details);
    }

    @ExceptionHandler(AopInvocationException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ResponseDto<Object>> handleAopInvocationException(AopInvocationException ex, WebRequest request){
        Map<String,Object> details = new HashMap<>();
        details.put("timestamp",LocalDateTime.now());
        details.put("details",ex.getMessage());
        details.put("path",request.getDescription(false));
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", details);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseDto<Object>> handleAllExceptions(Exception ex, WebRequest request){
        Map<String,Object> details = new HashMap<>();
        details.put("timestamp",LocalDateTime.now());
        details.put("message", ex.getMessage());
        details.put("path", request != null ? request.getDescription(false) : "");
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", details);
    }

    @ExceptionHandler(UserExists.class)
    public ResponseEntity<ResponseDto<Object>> handleUserExists(UserExists userExists, WebRequest request){
        Map<String,Object> details = new HashMap<>();
        details.put("timestamp",LocalDateTime.now());
        details.put("message", userExists.getMessage());
        details.put("path", request != null ? request.getDescription(false) : "");
        return build(HttpStatus.CONFLICT, "User already exists", details);
    }
}
