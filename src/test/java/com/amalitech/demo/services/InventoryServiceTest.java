package com.amalitech.demo.services;

import com.amalitech.demo.dao.interfaces.InventoryDao;
import com.amalitech.demo.dto.request.InventoryRequest;
import com.amalitech.demo.dto.response.InventoryResponse;
import com.amalitech.demo.exceptions.EntityNotFoundException;
import com.amalitech.demo.mapper.InventoryMapper;
import com.amalitech.demo.models.Inventory;
import com.amalitech.demo.models.Product;
import com.amalitech.demo.services.interfaces.ProductServiceInterface;
import com.amalitech.demo.utils.Sorter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InventoryServiceTest {

    @Mock
    private InventoryDao inventoryDao;

    @Mock
    private ProductServiceInterface productService;

    @Mock
    private InventoryMapper inventoryMapper;

    @Mock
    private Sorter<Inventory> sorter;

    @InjectMocks
    private InventoryService inventoryService;

    @Test
    void createInventory_success() {
        Product p = new Product(); p.setId(2L);
        InventoryRequest req = new InventoryRequest(); req.setProductId(2L); req.setStockQuantity(5);
        when(productService.getProductById(2L)).thenReturn(p);
        when(inventoryDao.existsByProductId(2L)).thenReturn(false);
        Inventory inv = new Inventory(); when(inventoryMapper.toEntity(req)).thenReturn(inv);
        when(inventoryDao.save(inv)).thenReturn(10L);
        when(inventoryMapper.toResponse(inv)).thenReturn(new InventoryResponse(10L,2L,5,0,"IN_STOCK", null));

        InventoryResponse resp = inventoryService.createInventory(req);
        assertNotNull(resp);
        verify(inventoryDao, times(1)).save(inv);
    }

    @Test
    void createInventory_duplicate_throws() {
        Product p = new Product(); p.setId(2L);
        InventoryRequest req = new InventoryRequest(); req.setProductId(2L); req.setStockQuantity(5);
        when(productService.getProductById(2L)).thenReturn(p);
        when(inventoryDao.existsByProductId(2L)).thenReturn(true);
        assertThrows(IllegalArgumentException.class, () -> inventoryService.createInventory(req));
        verify(inventoryDao, never()).save(any());
    }

    @Test
    void getInventoryById_success() {
        Inventory inv = new Inventory(); inv.setId(3L);
        when(inventoryDao.findById(3L)).thenReturn(Optional.of(inv));
        when(inventoryMapper.toResponse(inv)).thenReturn(new InventoryResponse(3L,1L,10,0,"IN_STOCK", null));
        InventoryResponse resp = inventoryService.getInventoryById(3L);
        assertNotNull(resp);
        verify(inventoryDao, times(1)).findById(3L);
    }

    @Test
    void getAllInventories_sorted() {
        Inventory a = new Inventory(); a.setId(1L);
        Inventory b = new Inventory(); b.setId(2L);
        when(inventoryDao.findAll()).thenReturn(List.of(b,a));
        when(sorter.sort(anyList(), any())).thenReturn(List.of(a,b));
        when(inventoryMapper.toResponse(any())).thenReturn(new InventoryResponse(1L,1L,10,0,"IN_STOCK", null));
        var list = inventoryService.getAllInventories();
        assertNotNull(list);
        verify(sorter, times(1)).sort(anyList(), any());
    }

    @Test
    void updateInventory_success() {
        Inventory existing = new Inventory(); existing.setId(4L);
        when(inventoryDao.findById(4L)).thenReturn(Optional.of(existing));
        InventoryRequest req = new InventoryRequest(); req.setProductId(1L); req.setStockQuantity(8); req.setReservedQuantity(0);
        Product p = new Product(); p.setId(1L);
        when(productService.getProductById(1L)).thenReturn(p);
        when(inventoryMapper.toResponse(existing)).thenReturn(new InventoryResponse(4L,1L,8,0,"IN_STOCK", null));
        var resp = inventoryService.updateInventory(4L, req);
        assertNotNull(resp);
        verify(inventoryDao, times(1)).update(existing);
    }

    @Test
    void deleteInventory_notFound_throws() {
        when(inventoryDao.findById(999L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> inventoryService.deleteInventory(999L));
    }
}
