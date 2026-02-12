package com.amalitech.demo.services;

import com.amalitech.demo.dao.interfaces.*;
import com.amalitech.demo.dto.OrderStatus;
import com.amalitech.demo.dto.request.OrderItemRequest;
import com.amalitech.demo.dto.request.OrderRequest;
import com.amalitech.demo.dto.response.OrderResponse;
import com.amalitech.demo.exceptions.EntityNotFoundException;
import com.amalitech.demo.mapper.OrdersMapper;
import com.amalitech.demo.models.*;
import com.amalitech.demo.utils.Sorter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrdersDao ordersDao;

    @Mock
    private OrdersMapper ordersMapper;

    @Mock
    private ProductDao productDao;

    @Mock
    private InventoryDao inventoryDao;

    @Mock
    private UserDao userDao;

    @Mock
    private OrderItemDao orderItemDao;

    @Mock
    private Sorter<Orders> sorter;

    @InjectMocks
    private OrderService orderService;

    @Test
    void createOrder_success_reducesInventory_and_savesOrder() throws Exception {
        // Arrange
        User user = new User(); user.setId(1L);
        Product prod = new Product(); prod.setId(10L); prod.setPrice(5.0);
        Inventory inv = new Inventory(); inv.setId(100L); inv.setProduct(prod); inv.setStockQuantity(10);

        OrderItemRequest itemReq = new OrderItemRequest(); itemReq.setProductId(prod.getId()); itemReq.setQuantity(2);
        OrderRequest req = new OrderRequest(); req.setUserId(user.getId()); req.setItems(List.of(itemReq));

        when(userDao.findById(user.getId())).thenReturn(Optional.of(user));
        when(productDao.findById(prod.getId())).thenReturn(Optional.of(prod));
        when(inventoryDao.findByProductId(prod.getId())).thenReturn(Optional.of(inv));
        when(ordersDao.save(any())).thenReturn(55L);
        when(ordersMapper.toResponse((Orders) any())).thenReturn(new OrderResponse(55L, user.getId(), "pending", 10.0, List.of(), LocalDateTime.now()));

        // Act
        OrderResponse resp = orderService.createOrder(req);

        // Assert
        assertNotNull(resp);
        assertEquals(55L, resp.id());
        // inventory updated (stock decreased by 2)
        verify(inventoryDao, times(1)).update(any(Inventory.class));
        verify(ordersDao, times(1)).save(any());
    }

    @Test
    void createOrder_insufficientStock_throws() {
        User user = new User(); user.setId(1L);
        Product prod = new Product(); prod.setId(10L); prod.setPrice(5.0);
        Inventory inv = new Inventory(); inv.setId(100L); inv.setProduct(prod); inv.setStockQuantity(1);

        OrderItemRequest itemReq = new OrderItemRequest(); itemReq.setProductId(prod.getId()); itemReq.setQuantity(2);
        OrderRequest req = new OrderRequest(); req.setUserId(user.getId()); req.setItems(List.of(itemReq));

        when(userDao.findById(user.getId())).thenReturn(Optional.of(user));
        when(productDao.findById(prod.getId())).thenReturn(Optional.of(prod));
        when(inventoryDao.findByProductId(prod.getId())).thenReturn(Optional.of(inv));

        assertThrows(IllegalArgumentException.class, () -> orderService.createOrder(req));
        verify(inventoryDao, never()).update(any());
    }

    @Test
    void updateOrderStatus_cancel_restoresInventory_and_updatesOrder() throws Exception {
        // Arrange an order with items
        Orders order = new Orders(); order.setId(2L);
        OrderItem item = new OrderItem();
        Product prod = new Product(); prod.setId(20L);
        item.setProduct(prod); item.setQuantity(3);
        order.setItems(List.of(item));
        order.setStatus(OrderStatus.pending);

        Inventory inv = new Inventory(); inv.setProduct(prod); inv.setStockQuantity(5);
        when(ordersDao.findById(2L)).thenReturn(Optional.of(order));
        when(inventoryDao.findByProductId(prod.getId())).thenReturn(Optional.of(inv));

        // Act
        OrderResponse resp = orderService.updateOrderStatus(2L, OrderStatus.cancelled);

        // Assert
        verify(inventoryDao, times(1)).update(any(Inventory.class));
        verify(ordersDao, times(1)).update(order);
    }

    @Test
    void getAllOrders_withSorting_usesSorter_and_maps() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("totalAmount").descending());
        Orders o1 = new Orders(); o1.setId(1L); o1.setTotalAmount(5.0);
        Orders o2 = new Orders(); o2.setId(2L); o2.setTotalAmount(15.0);
        when(ordersDao.findAll(10, 0)).thenReturn(List.of(o1, o2));
        when(sorter.sort(anyList(), any())).thenReturn(List.of(o2, o1));
        when(ordersMapper.toResponse((Orders) any())).thenReturn(new OrderResponse(2L, 1L, "pending", 15.0, List.of(), LocalDateTime.now()));

        var page = orderService.getAllOrders(pageable);
        assertNotNull(page);
        assertEquals(2, page.getContent().size());
        verify(sorter, times(1)).sort(anyList(), any());
        verify(ordersMapper, times(2)).toResponse(any(Orders.class));
    }

    @Test
    void getOrderByUserId_empty_throws() {
        when(ordersDao.findByUserId(5L)).thenReturn(List.of());
        assertThrows(EntityNotFoundException.class, () -> orderService.getOrderByUserId(5L));
    }

    @Test
    void deleteOrder_existing_deletes() throws Exception {
        Orders order = new Orders(); order.setId(7L);
        when(ordersDao.findById(7L)).thenReturn(Optional.of(order));
        orderService.deleteOrder(7L);
        verify(ordersDao, times(1)).deleteById(7L);
    }
}
