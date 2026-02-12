package com.amalitech.demo.services;

import com.amalitech.demo.dao.interfaces.InventoryDao;
import com.amalitech.demo.dto.request.InventoryRequest;
import com.amalitech.demo.dto.response.InventoryResponse;
import com.amalitech.demo.exceptions.EntityNotFoundException;
import com.amalitech.demo.mapper.InventoryMapper;
import com.amalitech.demo.models.Inventory;
import com.amalitech.demo.models.Product;
import com.amalitech.demo.services.interfaces.InventoryServiceInterface;
import com.amalitech.demo.services.interfaces.ProductServiceInterface;
import com.amalitech.demo.utils.Sorter;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InventoryService implements InventoryServiceInterface {

    private final InventoryDao inventoryDao;
    private final ProductServiceInterface productService;
    private final InventoryMapper inventoryMapper;
    private final Sorter<Inventory> sorter;

    public InventoryService(InventoryDao inventoryDao, ProductServiceInterface productService, InventoryMapper inventoryMapper, Sorter<Inventory> sorter){
        this.inventoryDao = inventoryDao;
        this.productService = productService;
        this.inventoryMapper = inventoryMapper;
        this.sorter = sorter;
    }

    @Override
    @CachePut(value="inventory", key="#result.id")
    public InventoryResponse createInventory(InventoryRequest request) {
        Product product = productService.getProductById(request.getProductId());
        if( inventoryDao.existsByProductId(product.getId())){
            throw new IllegalArgumentException("inventory with given product already exists");
        }
        Inventory inventory = inventoryMapper.toEntity(request);
        inventory.setProduct(product);
        inventoryDao.save(inventory);
        return inventoryMapper.toResponse(inventory);
    }

    @Override
    @Cacheable(value="inventory", key="#id",sync = true)
    public InventoryResponse getInventoryById(Long id) {
        Inventory inv = inventoryDao.findById(id).orElseThrow(() -> new EntityNotFoundException("Inventory not found"));
        return inventoryMapper.toResponse(inv);
    }

    @Override
    public List<InventoryResponse> getAllInventories() {
        List<Inventory> list = inventoryDao.findAll();
        if (list == null || list.isEmpty()) return List.of();
        List<Inventory> sorted = sorter.sort(list, Comparator.comparing(i -> i.getProduct() == null ? null : i.getProduct().getId(), Comparator.nullsLast(Long::compareTo)));
        return sorted.stream().map(inventoryMapper::toResponse).collect(Collectors.toList());
    }

    @Override
    @CachePut(value = "inventory",key = "#id")
    public InventoryResponse updateInventory(Long id, InventoryRequest request) {
        Inventory existingInventory = inventoryDao.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Inventory not found"));

        Product product = productService.getProductById(request.getProductId());
        existingInventory.setProduct(product);
        existingInventory.setStockQuantity(request.getStockQuantity());
        existingInventory.setReservedQuantity(request.getReservedQuantity());
        existingInventory.setStockStatus(request.getStockStatus());

        inventoryDao.update(existingInventory);
        return inventoryMapper.toResponse(existingInventory);
    }

    @Override
    @CachePut(value = "inventory",key = "#id")
    public void deleteInventory(Long id){
        Inventory inventory = inventoryDao.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Inventory not found"));

        inventoryDao.deleteById(id);
    }
}
