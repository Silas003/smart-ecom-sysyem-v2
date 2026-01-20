package com.amalitech.demo.mapper;

import com.amalitech.demo.dto.UserRequest;
import com.amalitech.demo.dto.UserResponse;
import com.amalitech.demo.models.User;
import jakarta.validation.constraints.NotNull;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toEntity(UserRequest req);
     UserResponse toResponse(User dto);
    List<UserResponse> toResponse(List<User> dtoList);
}

