package com.amalitech.demo.mapper;

import com.amalitech.demo.dto.UserRequest;
import com.amalitech.demo.models.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toEntity(UserRequest req);
}
