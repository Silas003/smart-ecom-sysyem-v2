package com.amalitech.demo.mapper;

import com.amalitech.demo.dto.OrderResponse;
import com.amalitech.demo.models.Orders;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(componentModel="spring")
public interface OrdersMapper{
    @Mapping(target="userId",source = "user.id")
    OrderResponse toResponse(Orders orders);

    @Mapping(target = "userId",source="user.id")
    List<OrderResponse> toResponse(List<Orders> orders);

    @Mapping(target = "userId",source = "user.id")
    Page<OrderResponse> toResponse(Page<Orders> orders);

}
