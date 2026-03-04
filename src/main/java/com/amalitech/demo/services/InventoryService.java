package com.amalitech.demo.services;

import com.amalitech.demo.dto.request.InventoryRequest;
import com.amalitech.demo.dto.response.InventoryResponse;
import com.amalitech.demo.exceptions.EntityNotFoundException;
import com.amalitech.demo.mapper.InventoryMapper;
import com.amalitech.demo.models.Inventory;
import com.amalitech.demo.models.Product;
import com.amalitech.demo.repository.InventoryRepository;
import com.amalitech.demo.services.interfaces.InventoryServiceInterface;
import com.amalitech.demo.services.interfaces.ProductServiceInterface;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class InventoryService implements InventoryServiceInterface {

    private final InventoryRepository inventoryRepository;
    private final ProductServiceInterface productService;
    private final InventoryMapper inventoryMapper;

    public InventoryService(InventoryRepository inventoryRepository, ProductServiceInterface productService, InventoryMapper inventoryMapper) {
        this.inventoryRepository = inventoryRepository;
        this.productService = productService;
        this.inventoryMapper = inventoryMapper;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    @CachePut(value = "inventory", key = "#result.id")
    public InventoryResponse createInventory(InventoryRequest request) {
        Product product = productService.getProductById(request.getProductId());
        if (inventoryRepository.existsByProductId(product.getId())) {
            throw new IllegalArgumentException("inventory with given product already exists");
        }
        Inventory inventory = inventoryMapper.toEntity(request);
        inventory.setProduct(product);
        Inventory saved = inventoryRepository.save(inventory);
        return inventoryMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "inventory", key = "#id", sync = true)
    public InventoryResponse getInventoryById(Long id) {
        Inventory inv = inventoryRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Inventory not found"));
        return inventoryMapper.toResponse(inv);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryResponse> getAllInventories() {
        List<Inventory> list = inventoryRepository.findAllByOrderByProduct_IdAsc();
        if (list == null || list.isEmpty()) return List.of();
        return list.stream().map(inventoryMapper::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    @CachePut(value = "inventory", key = "#id")
    public InventoryResponse updateInventory(Long id, InventoryRequest request) {
        Inventory existingInventory = inventoryRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Inventory not found"));

        Product product = productService.getProductById(request.getProductId());
        existingInventory.setProduct(product);
        existingInventory.setStockQuantity(request.getStockQuantity());
        existingInventory.setReservedQuantity(request.getReservedQuantity());
        existingInventory.setStockStatus(request.getStockStatus());

        Inventory saved = inventoryRepository.save(existingInventory);
        return inventoryMapper.toResponse(saved);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    @CachePut(value = "inventory", key = "#id")
    public void deleteInventory(Long id) {
        Inventory inventory = inventoryRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Inventory not found"));

        inventoryRepository.deleteById(inventory.getId());
    }

    @Override
    public Inventory getInventoryByProductId(Long productId) {
        return inventoryRepository.findByProduct_Id(productId);
    }
}
