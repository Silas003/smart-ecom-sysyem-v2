package com.amalitech.demo.mapper;


import com.amalitech.demo.dto.CartItemsReponse;
import com.amalitech.demo.models.CartItems;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CartItemMapper {
    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "cartId", source = "cart.id")
    CartItemsReponse toResponse(CartItems cartItems);
}
