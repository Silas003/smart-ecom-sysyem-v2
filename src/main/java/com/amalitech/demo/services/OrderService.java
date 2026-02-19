package com.amalitech.demo.services;

import com.amalitech.demo.dto.OrderStatus;
import com.amalitech.demo.dto.request.OrderItemRequest;
import com.amalitech.demo.dto.request.OrderRequest;
import com.amalitech.demo.dto.response.OrderResponse;
import com.amalitech.demo.exceptions.EntityNotFoundException;
import com.amalitech.demo.mapper.OrdersMapper;
import com.amalitech.demo.models.*;
import com.amalitech.demo.repository.InventoryRepository;
import com.amalitech.demo.repository.OrdersRepository;
import com.amalitech.demo.repository.ProductRepository;
import com.amalitech.demo.repository.UserRepository;
import com.amalitech.demo.security.CurrentUser;
import com.amalitech.demo.services.interfaces.OrderServiceInterface;
import com.amalitech.demo.services.specification.OrderSpecification;
import com.amalitech.demo.utils.Sorter;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class OrderService implements OrderServiceInterface {

    private final OrdersRepository ordersRepository;
    private final OrdersMapper ordersMapper;
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;
    private final UserRepository userRepository;

    private final Sorter<Orders> sorter;

    private static final Map<OrderStatus, Set<OrderStatus>> ALLOWED_TRANSITIONS =
            Map.of(
                    OrderStatus.pending, Set.of(OrderStatus.processing, OrderStatus.cancelled),
                    OrderStatus.processing, Set.of(OrderStatus.delivered, OrderStatus.cancelled),
                    OrderStatus.delivered, Set.of(),
                    OrderStatus.cancelled, Set.of()
            );

    private Long getCurrentUserIdOrThrow() {
        String email = CurrentUser.getEmail();
        if (email == null) {
            throw new AccessDeniedException("Unauthenticated");
        }
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AccessDeniedException("User not found for current principal"));
        return user.getId();
    }

    private boolean isCurrentUserAdmin() {
        var auth = CurrentUser.getAuthentication();
        return auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_admin"));
    }

    @Override
    @Cacheable(value = "ordersByUser", key = "#userId")
    public List<OrderResponse> getOrderByUserId(Long userId) {
        // Enforce ownership: non-admins can only see their own orders
        if (!isCurrentUserAdmin()) {
            Long currentUserId = getCurrentUserIdOrThrow();
            if (!currentUserId.equals(userId)) {
                throw new AccessDeniedException("Cannot access other users' orders");
            }
        }
        List<Orders> orders = ordersRepository.findByUserId(userId);
        if (orders == null || orders.isEmpty()) {
            throw new EntityNotFoundException("user does not have any orders");
        }
        return ordersMapper.toResponse(orders);
    }

    @Override
    @Cacheable(value = "order", key = "#id")
    public OrderResponse getOrderById(Long id) {
        Orders order = ordersRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("order not found"));
        // Enforce ownership for non-admins based on order.user.id
        if (!isCurrentUserAdmin()) {
            Long currentUserId = getCurrentUserIdOrThrow();
            User orderUser = order.getUser();
            if (orderUser == null || orderUser.getId() == null || !orderUser.getId().equals(currentUserId)) {
                throw new AccessDeniedException("Cannot access other users' orders");
            }
        }
        return ordersMapper.toResponse(order);
    }

    @Override
    @Cacheable(value = "orders", keyGenerator = "orderSearchKeyGenerator")
    public Page<OrderResponse> getAllOrders(Pageable pageable, Long userId, OrderStatus status, LocalDateTime start, LocalDateTime end) {
        Specification<Orders> spec = Specification.anyOf(OrderSpecification.hasUserId(userId))
                .and(OrderSpecification.hasStatus(status))
                .and(OrderSpecification.isBetween(start, end));

        Page<Orders> page = ordersRepository.findAll(spec, pageable);
        List<OrderResponse> content = page.getContent().stream()
                .map(ordersMapper::toResponse)
                .toList();
        return new PageImpl<>(content, pageable, page.getTotalElements());
    }



    @Override
    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(value = "order", key = "#orderId"),
                    @CacheEvict(value = "ordersByUser", key = "#result.userId"),
                    @CacheEvict(value = "orders", allEntries = true)
            }
    )
    public void deleteOrder(Long orderId) {
        ordersRepository.findById(orderId).orElseThrow(() -> new EntityNotFoundException("order not found"));
        ordersRepository.deleteById(orderId);
    }

    @Override
    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(value = "order", key = "#orderId"),
                    @CacheEvict(value = "ordersByUser", key = "#result.userId"),
                    @CacheEvict(value = "orders", allEntries = true)
            }
    )
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
        Orders saved = ordersRepository.save(order);

        return ordersMapper.toResponse(saved);
    }

//    @Override
//    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.REPEATABLE_READ)
//    public OrderResponse createOrder(OrderRequest req) {
//        // 1. Validate user
//        Long userId = req.getUserId();
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new EntityNotFoundException("User not found"));
//
//        // 2. Validate order items
//        if (req.getItems() == null || req.getItems().isEmpty()) {
//            throw new IllegalArgumentException("Order must contain at least one item");
//        }
//
//        // 3. Create order
//        Orders order = new Orders();
//        order.setUser(user);
//        order.setStatus(OrderStatus.pending);
//
//        // 4. Process order items and validate inventory
//        List<OrderItem> items = new ArrayList<>();
//        double total = 0.0;
//
//        for (OrderItemRequest itemReq : req.getItems()) {
//            // Validate product exists
//            Product product = productRepository.findById(itemReq.getProductId())
//                    .orElseThrow(() -> new EntityNotFoundException("Product not found with ID: " + itemReq.getProductId()));
//
//            // Validate inventory exists and has sufficient stock
//            Inventory inv = inventoryRepository.findByProductId(product.getId())
//                    .orElseThrow(() -> new EntityNotFoundException("Inventory not found for product ID: " + product.getId()));
//
//            if (inv.getStockQuantity() < itemReq.getQuantity()) {
//                throw new IllegalArgumentException("Insufficient stock for product ID: " + product.getId()
//                        + ". Available: " + inv.getStockQuantity() + ", Requested: " + itemReq.getQuantity());
//            }
//
//            // Create order item
//            OrderItem oi = new OrderItem();
//            oi.setOrder(order);
//            oi.setProduct(product);
//            oi.setQuantity(itemReq.getQuantity());
//            oi.setUnitPrice(product.getPrice());
//            oi.setTotalPrice(product.getPrice() * itemReq.getQuantity());
//            items.add(oi);
//
//            total += oi.getTotalPrice();
//
//            // Decrement inventory
//            inv.setStockQuantity(inv.getStockQuantity() - itemReq.getQuantity());
//            inventoryRepository.save(inv);
//        }
//
//        // 5. Set order details
//        order.setTotalAmount(total);
//        order.setItems(items);
//
//        // 6. Save order and cascade items
//        Orders savedOrder = ordersRepository.save(order);
//        return ordersMapper.toResponse(savedOrder);
//    }

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.REPEATABLE_READ)
    @Caching(
            put = {
                    @CachePut(value = "order", key = "#result.id"),
                    @CachePut(value = "ordersByUser", key = "#result.userId")
            },
            evict = {
                    @CacheEvict(value = "orders", allEntries = true)
            }
    )
    public OrderResponse createOrder(OrderRequest req) {

        User user = getUserOrThrow(req.getUserId());
        validateOrderItems(req.getItems());

        Orders order = buildOrder(user);

        List<OrderItem> orderItems = new ArrayList<>();
        double totalAmount = 0.0;

        for (OrderItemRequest itemReq : req.getItems()) {

            Product product = getProductOrThrow(itemReq.getProductId());
            Inventory inventory = getInventoryOrThrow(product.getId());

            validateStock(inventory, itemReq.getQuantity(), product.getId());

            OrderItem orderItem = buildOrderItem(order, product, itemReq.getQuantity());

            decrementInventory(inventory, itemReq.getQuantity());

            orderItems.add(orderItem);
            totalAmount += orderItem.getTotalPrice();
        }

        order.setItems(orderItems);
        order.setTotalAmount(totalAmount);

        Orders savedOrder = ordersRepository.save(order);

        return ordersMapper.toResponse(savedOrder);
    }


    public void restoreInventory(Orders order) {
        if (order.getItems() == null) return;
        for (OrderItem item : order.getItems()) {
            inventoryRepository.findByProductId(item.getProduct().getId())
                    .ifPresent(inv -> {
                        inv.setStockQuantity(inv.getStockQuantity() + item.getQuantity());
                        inventoryRepository.save(inv);
                    });
        }
    }

    @Override
    @Transactional
    public Page<OrderResponse> getUserOrdersWithinPeriod(Long userId, LocalDateTime start, LocalDateTime end, Pageable pageable) {
        // Enforce ownership: non-admins can only see their own orders
        if (!isCurrentUserAdmin()) {
            Long currentUserId = getCurrentUserIdOrThrow();
            if (!currentUserId.equals(userId)) {
                throw new AccessDeniedException("Cannot access other users' orders");
            }
        }

        Specification<Orders> spec = OrderSpecification.hasUserId(userId)
                .and(OrderSpecification.isBetween(start, end));

        Page<Orders> page = ordersRepository.findAll(spec, pageable);
        List<OrderResponse> content = page.getContent().stream()
                .map(ordersMapper::toResponse)
                .toList();
        return new PageImpl<>(content, pageable, page.getTotalElements());
    }

    @Override
    public Double getTotalRevenue(LocalDateTime start, LocalDateTime end) {
        return ordersRepository.calculateTotalRevenue(start, end);
    }

    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));
    }

    private void validateOrderItems(List<OrderItemRequest> items) {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Order must contain at least one item");
        }
    }

    private Orders buildOrder(User user) {
        Orders order = new Orders();
        order.setUser(user);
        order.setStatus(OrderStatus.pending);
        return order;
    }
    private Product getProductOrThrow(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Product not found with ID: " + productId));
    }
    private Inventory getInventoryOrThrow(Long productId) {
        return inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Inventory not found for product ID: " + productId));
    }
    private void validateStock(Inventory inventory, int requestedQty, Long productId) {

        if (inventory.getStockQuantity() < requestedQty) {
            throw new IllegalArgumentException(
                    String.format(
                            "Insufficient stock for product ID: %d. Available: %d, Requested: %d",
                            productId,
                            inventory.getStockQuantity(),
                            requestedQty
                    )
            );
        }
    }
    private OrderItem buildOrderItem(Orders order, Product product, int quantity) {

        OrderItem item = new OrderItem();

        item.setOrder(order);
        item.setProduct(product);
        item.setQuantity(quantity);
        item.setUnitPrice(product.getPrice());
        item.setTotalPrice(product.getPrice() * quantity);

        return item;
    }
    private void decrementInventory(Inventory inventory, int quantity) {

        inventory.setStockQuantity(inventory.getStockQuantity() - quantity);

        inventoryRepository.save(inventory);
    }



}
