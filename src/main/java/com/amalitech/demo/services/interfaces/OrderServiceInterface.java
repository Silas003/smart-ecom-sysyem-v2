package com.amalitech.demo.services.interfaces;

import com.amalitech.demo.dto.OrderStatus;
import com.amalitech.demo.dto.request.OrderRequest;
import com.amalitech.demo.dto.response.OrderResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface OrderServiceInterface {

    List<OrderResponse> getOrderByUserId(Long userId);

    OrderResponse getOrderById(Long id);

    Page<OrderResponse> getAllOrders(Pageable pageable);

    void deleteOrder(Long orderId);


    OrderResponse updateOrderStatus(Long orderId, OrderStatus newStatus);


    OrderResponse createOrder(OrderRequest req);

    /**
     * Restore inventory quantities for all items in the given order.
     * This method will look up the Inventory by product id and add back the ordered quantity.
     * It is transactional and will throw EntityNotFoundException if an Inventory for a product is missing.
     */

}
