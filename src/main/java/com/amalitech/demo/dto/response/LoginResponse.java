package com.amalitech.demo.dto.response;

import java.util.Map;

public record LoginResponse(Map<String,String> token, UserResponse user) {
}

