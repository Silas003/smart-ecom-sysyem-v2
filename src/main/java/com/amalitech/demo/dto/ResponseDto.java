package com.amalitech.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@Schema(description = "Standard API response wrapper")
public class ResponseDto<T> {
    @Schema(description = "HTTP status of the response", example = "200")
    private HttpStatus status;

    @Schema(description = "Human readable message", example = "request successful")
    private String message;

    @Schema(description = "Payload data")
    private T data;

    public ResponseDto(HttpStatus status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }


}
