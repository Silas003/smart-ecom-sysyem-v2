package com.amalitech.demo.services.interfaces;

import com.amalitech.demo.dto.OrderStatus;
import com.amalitech.demo.dto.request.OrderRequest;
import com.amalitech.demo.dto.response.OrderResponse;
import com.amalitech.demo.exceptions.EntityNotFoundException;
import com.amalitech.demo.models.Inventory;
import com.amalitech.demo.models.OrderItem;
import com.amalitech.demo.models.Orders;
import com.amalitech.demo.repository.InventoryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface OrderServiceInterface {
    InventoryRepository inventoryRepository = null;
    List<OrderResponse> getOrderByUserId(Long userId);

    OrderResponse getOrderById(Long id);

    Page<OrderResponse> getAllOrders(Pageable pageable);

    void deleteOrder(Long orderId);

    @Transactional
    OrderResponse updateOrderStatus(Long orderId, OrderStatus newStatus);

    @Transactional
    OrderResponse createOrder(OrderRequest req);

    /**
     * Restore inventory quantities for all items in the given order.
     * This method will look up the Inventory by product id and add back the ordered quantity.
     * It is transactional and will throw EntityNotFoundException if an Inventory for a product is missing.
     */

}
