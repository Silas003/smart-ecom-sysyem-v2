package com.amalitech.demo.mapper;

import com.amalitech.demo.dto.response.CartResponse;
import com.amalitech.demo.models.Cart;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CartMapper {
    @Mapping(source = "user.id", target = "userId")
    CartResponse toResponse(Cart cart);
}
