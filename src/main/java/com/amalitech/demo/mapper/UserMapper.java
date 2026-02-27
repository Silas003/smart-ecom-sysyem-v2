package com.amalitech.demo.mapper;

import com.amalitech.demo.dto.request.UserRequest;
import com.amalitech.demo.dto.response.UserResponse;
import com.amalitech.demo.models.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "provider" ,source = "provider")
    User toEntity(UserRequest req);
     UserResponse toResponse(User dto);
    List<UserResponse> toResponse(List<User> dtoList);
}

