package com.amalitech.demo.services;

import com.amalitech.demo.dto.InventoryRequest;
import com.amalitech.demo.exceptions.EntityNotFoundException;
import com.amalitech.demo.mapper.InventoryMapper;
import com.amalitech.demo.models.Inventory;
import com.amalitech.demo.models.Product;
import com.amalitech.demo.repository.InventoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public Inventory createInventory(InventoryRequest request) {
        Product product = productService.getProductById(request.getProductId());
        if( inventoryRepository.findByProductId(product.getId())){
            throw new IllegalArgumentException("inventory with given product already exists");
        }
        Inventory inventory = inventoryMapper.toEntity(request);
        inventory.setProduct(product);
        return inventoryRepository.save(inventory);
    }

    public Inventory getInventoryById(Long id) {
        return inventoryRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Inventory not found"));
    }

    public List<Inventory> getAllInventories() {
        return inventoryRepository.findAll();
    }

    public Inventory updateInventory(Long id, InventoryRequest request) {
        Inventory existingInventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Inventory not found"));

        Product product = productService.getProductById(request.getProductId());
        existingInventory.setProduct(product);
        existingInventory.setStockQuantity(request.getStockQuantity());
        existingInventory.setReservedQuantity(request.getReservedQuantity());
        existingInventory.setStockStatus(request.getStockStatus());

        return inventoryRepository.save(existingInventory);
    }

    public void deleteInventory(Long id){
        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Inventory not found"));

        inventoryRepository.deleteById(id);
    }
}
