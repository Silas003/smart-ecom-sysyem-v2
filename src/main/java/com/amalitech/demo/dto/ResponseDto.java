package com.amalitech.demo.dto;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class ResponseDto<T> {
    private HttpStatus status;
    private String message;
    private T data;

    public ResponseDto(HttpStatus status,String message,T data){
        this.status = status;
        this.message = message;
        this.data = data;

    }
}


