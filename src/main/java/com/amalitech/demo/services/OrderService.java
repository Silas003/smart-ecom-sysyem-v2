package com.amalitech.demo.services;

import com.amalitech.demo.dto.*;
import com.amalitech.demo.dto.request.OrderItemRequest;
import com.amalitech.demo.dto.request.OrderRequest;
import com.amalitech.demo.dto.response.OrderResponse;
import com.amalitech.demo.exceptions.EntityNotFoundException;
import com.amalitech.demo.mapper.OrdersMapper;
import com.amalitech.demo.models.*;
import com.amalitech.demo.repository.*;
import com.amalitech.demo.services.interfaces.OrderServiceInterface;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@AllArgsConstructor
public class OrderService implements OrderServiceInterface {

    private final OrdersRepository ordersRepository;
    private final OrdersMapper ordersMapper;
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;
    private final UserRepository userRepository;

    private static final Map<OrderStatus, Set<OrderStatus>> ALLOWED_TRANSITIONS =
            Map.of(
                    OrderStatus.pending, Set.of(OrderStatus.processing, OrderStatus.cancelled),
                    OrderStatus.processing, Set.of(OrderStatus.delivered, OrderStatus.cancelled),
                    OrderStatus.delivered, Set.of(),
                    OrderStatus.cancelled, Set.of()
            );

    @Override
    @Cacheable(value="ordersByUser", key="#userId", sync = true)
    public List<OrderResponse> getOrderByUserId(Long userId){
        List<Orders> orders = ordersRepository.findByUser_IdWithItemsAndProducts(userId).orElseThrow(
                ()-> new EntityNotFoundException("user does not have any orders")
        );
        // Use OrdersMapper to convert entities to DTOs (ensures productId is populated)
        return ordersMapper.toResponse(orders);
    }

    @Override
    @Cacheable(value = "order", key = "#id")
    public OrderResponse getOrderById(Long id){
        Orders order = ordersRepository.findByIdWithItemsAndProducts(id).orElseThrow(()-> new EntityNotFoundException("order not found"));
        return ordersMapper.toResponse(order);
    }

    @Override
    public Page<OrderResponse> getAllOrders(Pageable pageable) {
        Page<Orders> orders = ordersRepository.findAll(pageable);
        // Ensure items and products are fetched for each order before mapping to DTOs
        List<OrderResponse> content = orders.getContent().stream().map(o -> {
            Orders full = ordersRepository.findByIdWithItemsAndProducts(o.getId()).orElse(o);
            return ordersMapper.toResponse(full);
        }).toList();

        return new org.springframework.data.domain.PageImpl<>(content, pageable, orders.getTotalElements());
    }

    @Override
    @Caching(
            evict = {
                    @CacheEvict(value = "order", key = "#orderId"),
                    @CacheEvict(value = "ordersByUser", allEntries = true)
            }
    )
    public void deleteOrder(Long orderId) {
        Orders order = ordersRepository.findById(orderId)
                .orElseThrow(()-> new EntityNotFoundException("order not found"));
        ordersRepository.delete(order);
    }

    @Caching(
            evict = {
                    @CacheEvict(value = "order", key = "#orderId"),
                    @CacheEvict(value = "ordersByUser", allEntries = true)
            }
    )
    @Transactional
    @Override
    public OrderResponse updateOrderStatus(Long orderId, OrderStatus newStatus) {
        Orders order = ordersRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));

        OrderStatus currentStatus = order.getStatus();

        if (!ALLOWED_TRANSITIONS.getOrDefault(currentStatus, Collections.emptySet()).contains(newStatus)) {
            throw new IllegalStateException(
                    "Cannot change order status from " + currentStatus + " to " + newStatus
            );
        }

        // If transitioning into cancelled from a non-cancelled state, restore inventory
        if (currentStatus != OrderStatus.cancelled && newStatus == OrderStatus.cancelled) {
            restoreInventory(order);
        }

        order.setStatus(newStatus);
        Orders updated = ordersRepository.save(order);

        return ordersMapper.toResponse(updated);
    }

    @CachePut(value = "orderByUser",key="#result.userId")
    @Transactional
    @Override
    public OrderResponse createOrder(OrderRequest req) {
        Long userId = req.getUserId();
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found"));
        if (req.getItems() == null || req.getItems().isEmpty()) {
            throw new IllegalArgumentException("Order must contain at least one item");
        }

        Orders order = new Orders();
        order.setUser(user);
        order.setStatus(OrderStatus.pending);

        List<OrderItem> items = new ArrayList<>();
        double total = 0.0;

        for (OrderItemRequest it : req.getItems()) {
            Product product = productRepository.findById(it.getProductId()).orElseThrow(() -> new EntityNotFoundException("Product not found"));
            Inventory inv = inventoryRepository.findByProduct_Id(product.getId()).orElseThrow(() -> new EntityNotFoundException("Inventory not found for product"));
            if (inv.getStockQuantity() < it.getQuantity()) {
                throw new IllegalArgumentException("Insufficient stock for product id: " + product.getId());
            }
            inv.setStockQuantity(inv.getStockQuantity() - it.getQuantity());
            inventoryRepository.save(inv);

            OrderItem oi = new OrderItem();
            oi.setOrder(order);
            oi.setProduct(product);
            oi.setQuantity(it.getQuantity());
            oi.setUnitPrice(product.getPrice());
            oi.setTotalPrice(product.getPrice() * it.getQuantity());
            items.add(oi);
            total += oi.getTotalPrice();
        }

        order.setTotalAmount(total);
        order.setItems(items);

        Orders saved = ordersRepository.save(order);
        return ordersMapper.toResponse(saved);
    }

}
