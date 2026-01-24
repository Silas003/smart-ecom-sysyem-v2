package com.amalitech.demo.services;


import com.amalitech.demo.dto.OrderResponse;
import com.amalitech.demo.exceptions.EntityNotFoundException;
import com.amalitech.demo.mapper.OrdersMapper;
import com.amalitech.demo.models.Orders;
import com.amalitech.demo.repository.OrdersRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class OrderService {

    private OrdersRepository ordersRepository;
    private OrdersMapper ordersMapper;


    public List<OrderResponse> getOrderByUserId(Long userId){
        List<Orders> orders = ordersRepository.findByUser_Id(userId).orElseThrow(
                ()-> new EntityNotFoundException("user does not have any orders")
        );
        List<OrderResponse> orderResponses = ordersMapper.toResponse(orders);
        return orderResponses;
    }

    public OrderResponse getOrderById(Long id){
        return ordersMapper.toResponse(
                ordersRepository.findById(id)
                        .orElseThrow(()-> new EntityNotFoundException("order not found"))
        );
    }

    public List<OrderResponse> getAllOrders(Pageable pageable) {
        Page<Orders> orders = ordersRepository.findAll(pageable);
        List<OrderResponse> orderResponses= orders.stream().map(o->ordersMapper.toResponse(o)).toList();
        return orderResponses;
    }
}
