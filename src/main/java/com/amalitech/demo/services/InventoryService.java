package com.amalitech.demo.services;

import com.amalitech.demo.dto.InventoryRequest;
import com.amalitech.demo.dto.InventoryResponse;
import com.amalitech.demo.exceptions.EntityNotFoundException;
import com.amalitech.demo.mapper.InventoryMapper;
import com.amalitech.demo.models.Inventory;
import com.amalitech.demo.models.Product;
import com.amalitech.demo.repository.InventoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final ProductService productService;
    private final InventoryMapper inventoryMapper;

    public InventoryService(InventoryRepository inventoryRepository, ProductService productService, InventoryMapper inventoryMapper){
        this.inventoryRepository = inventoryRepository;
        this.productService = productService;
        this.inventoryMapper = inventoryMapper;
    }

    public InventoryResponse createInventory(InventoryRequest request) {
        Product product = productService.getProductById(request.getProductId());
        if( inventoryRepository.existsByProductId(product.getId())){
            throw new IllegalArgumentException("inventory with given product already exists");
        }
        Inventory inventory = inventoryMapper.toEntity(request);
        inventory.setProduct(product);
        Inventory saved = inventoryRepository.save(inventory);
        return inventoryMapper.toResponse(saved);
    }

    public InventoryResponse getInventoryById(Long id) {
        Inventory inv = inventoryRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Inventory not found"));
        return inventoryMapper.toResponse(inv);
    }

    public List<InventoryResponse> getAllInventories() {
        return inventoryRepository.findAll().stream().map(inventoryMapper::toResponse).collect(Collectors.toList());
    }

    public InventoryResponse updateInventory(Long id, InventoryRequest request) {
        Inventory existingInventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Inventory not found"));

        Product product = productService.getProductById(request.getProductId());
        existingInventory.setProduct(product);
        existingInventory.setStockQuantity(request.getStockQuantity());
        existingInventory.setReservedQuantity(request.getReservedQuantity());
        existingInventory.setStockStatus(request.getStockStatus());

        Inventory saved = inventoryRepository.save(existingInventory);
        return inventoryMapper.toResponse(saved);
    }

    public void deleteInventory(Long id){
        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Inventory not found"));

        inventoryRepository.deleteById(id);
    }
}
