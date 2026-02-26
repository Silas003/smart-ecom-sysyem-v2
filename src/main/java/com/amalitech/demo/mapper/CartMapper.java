package com.amalitech.demo.mapper;

import com.amalitech.demo.dto.response.CartItemsReponse;
import com.amalitech.demo.dto.response.CartResponse;
import com.amalitech.demo.models.Cart;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CartMapper {
    @Mapping(source = "cart.user.id", target = "userId")
    @Mapping(source = "cart.status", target = "status")
    CartResponse toResponse(Cart cart, List<CartItemsReponse> items);
}
