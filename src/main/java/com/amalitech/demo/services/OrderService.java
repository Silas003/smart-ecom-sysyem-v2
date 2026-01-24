package com.amalitech.demo.services;


import com.amalitech.demo.dto.OrderResponse;
import com.amalitech.demo.dto.OrderStatus;
import com.amalitech.demo.exceptions.EntityNotFoundException;
import com.amalitech.demo.mapper.OrdersMapper;
import com.amalitech.demo.models.Orders;
import com.amalitech.demo.repository.OrdersRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;


@Service
@AllArgsConstructor
public class OrderService {

    private OrdersRepository ordersRepository;
    private OrdersMapper ordersMapper;
    private static final Map<OrderStatus, Set<OrderStatus>> ALLOWED_TRANSITIONS =
            Map.of(
                    OrderStatus.pending, Set.of(OrderStatus.processing, OrderStatus.cancelled),
                    OrderStatus.processing, Set.of(OrderStatus.delivered, OrderStatus.cancelled),
                    OrderStatus.delivered, Set.of(),
                    OrderStatus.cancelled, Set.of()
            );



    public List<OrderResponse> getOrderByUserId(Long userId){
        List<Orders> orders = ordersRepository.findByUser_Id(userId).orElseThrow(
                ()-> new EntityNotFoundException("user does not have any orders")
        );
        return ordersMapper.toResponse(orders);
    }

    public OrderResponse getOrderById(Long id){
        return ordersMapper.toResponse(
                ordersRepository.findById(id)
                        .orElseThrow(()-> new EntityNotFoundException("order not found"))
        );
    }

    public Page<OrderResponse> getAllOrders(Pageable pageable) {
        Page<Orders> orders = ordersRepository.findAll(pageable);
        return orders.map(o-> ordersMapper.toResponse(o));
    }

    public void deleteOrder(Long orderId) {
        Orders order = ordersRepository.findById(orderId)
                .orElseThrow(()-> new EntityNotFoundException("order not found"));
        ordersRepository.delete(order);
    }
    public OrderResponse updateOrderStatus(Long orderId, OrderStatus newStatus) {
        Orders order = ordersRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));

        OrderStatus currentStatus = order.getStatus();

        if (!ALLOWED_TRANSITIONS.get(currentStatus).contains(newStatus)) {
            throw new IllegalStateException(
                    "Cannot change order status from " + currentStatus + " to " + newStatus
            );
        }

        order.setStatus(newStatus);
        Orders updated = ordersRepository.save(order);

        return ordersMapper.toResponse(updated);
    }

}
