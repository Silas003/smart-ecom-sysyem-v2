package com.amalitech.demo.services;

import com.amalitech.demo.dto.*;
import com.amalitech.demo.dto.request.OrderItemRequest;
import com.amalitech.demo.dto.request.OrderRequest;
import com.amalitech.demo.dto.response.OrderResponse;
import com.amalitech.demo.exceptions.EntityNotFoundException;
import com.amalitech.demo.mapper.OrdersMapper;
import com.amalitech.demo.models.*;
import com.amalitech.demo.dao.interfaces.*;
import com.amalitech.demo.services.interfaces.OrderServiceInterface;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@AllArgsConstructor
public class OrderService implements OrderServiceInterface {

    private final OrdersDao ordersDao;
    private final OrdersMapper ordersMapper;
    private final ProductDao productDao;
    private final InventoryDao inventoryDao;
    private final UserDao userDao;
    private final OrderItemDao orderItemDao;

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
        List<Orders> orders = ordersDao.findByUserId(userId);
        if(orders == null || orders.isEmpty()){
            throw new EntityNotFoundException("user does not have any orders");
        }
        return ordersMapper.toResponse(orders);
    }

    @Override
    @Cacheable(value = "order", key = "#id")
    public OrderResponse getOrderById(Long id){
        Orders order = ordersDao.findById(id).orElseThrow(()-> new EntityNotFoundException("order not found"));
        return ordersMapper.toResponse(order);
    }

    @Override
    public Page<OrderResponse> getAllOrders(Pageable pageable) {
        int pageSize = pageable.getPageSize();
        int pageNumber = pageable.getPageNumber();
        int offset = pageNumber * pageSize;
        List<Orders> orders = ordersDao.findAll(pageSize, offset);
        List<OrderResponse> content = orders.stream().map(ordersMapper::toResponse).toList();
        long total = content.size();
        return new PageImpl<>(content, pageable, total);
    }

    @Override
    @Caching(
            evict = {
                    @CacheEvict(value = "order", key = "#orderId"),
                    @CacheEvict(value = "ordersByUser", allEntries = true)
            }
    )
    public void deleteOrder(Long orderId) {
        Orders order = ordersDao.findById(orderId)
                .orElseThrow(()-> new EntityNotFoundException("order not found"));
        try {
            ordersDao.deleteById(orderId);
        } catch (Exception e){
            throw new RuntimeException(e);
        }
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
        Orders order = ordersDao.findById(orderId)
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
        try {
            ordersDao.update(order);
        } catch (Exception e){
            throw new RuntimeException(e);
        }

        return ordersMapper.toResponse(order);
    }

    @CachePut(value = "orderByUser",key="#result.userId")
    @Transactional
    @Override
    public OrderResponse createOrder(OrderRequest req) {
        Long userId = req.getUserId();
        User user = userDao.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found"));
        if (req.getItems() == null || req.getItems().isEmpty()) {
            throw new IllegalArgumentException("Order must contain at least one item");
        }

        Orders order = new Orders();
        order.setUser(user);
        order.setStatus(OrderStatus.pending);

        List<OrderItem> items = new ArrayList<>();
        double total = 0.0;

        for (OrderItemRequest it : req.getItems()) {
            Product product = productDao.findById(it.getProductId()).orElseThrow(() -> new EntityNotFoundException("Product not found"));
            Inventory inv = inventoryDao.findByProductId(product.getId()).orElseThrow(() -> new EntityNotFoundException("Inventory not found for product"));
            if (inv.getStockQuantity() < it.getQuantity()) {
                throw new IllegalArgumentException("Insufficient stock for product id: " + product.getId());
            }
            inv.setStockQuantity(inv.getStockQuantity() - it.getQuantity());
            inventoryDao.update(inv);

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

        try {
            long id = ordersDao.save(order);
            order.setId(id);
            return ordersMapper.toResponse(order);
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public void restoreInventory(Orders order) {
        if (order.getItems() == null) return;
        for (OrderItem item : order.getItems()) {
            Inventory inv = inventoryDao.findByProductId(item.getProduct().getId()).orElse(null);
            if (inv != null) {
                inv.setStockQuantity(inv.getStockQuantity() + item.getQuantity());
                inventoryDao.update(inv);
            }
        }
    }

}
