package com.amalitech.demo.services;

import com.amalitech.demo.exceptions.EntityNotFoundException;
import com.amalitech.demo.models.Inventory;
import com.amalitech.demo.repository.InventoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InventoryService {

    private static InventoryRepository inventoryRepository;

    public InventoryService(InventoryRepository inventoryRepository){
        this.inventoryRepository = inventoryRepository;
    }

    public static Inventory createInventory(Inventory inventory) {
        if( inventoryRepository.findByProductId(inventory.getProduct().getId())){
            throw new IllegalArgumentException("inventory with given product already exists");
        }
        return inventoryRepository.save(inventory);
    }
    public static Inventory getInventoryById(Long id) {
        return inventoryRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Inventory not found"));
    }

    public static List<Inventory> getAllInventories() {
        return inventoryRepository.findAll();
    }

    public Inventory updateInventory(Long id, Inventory inventory) {
        Inventory existingInventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Inventory not found"));

        existingInventory.setProduct(inventory.getProduct());
        existingInventory.setStockQuantity(inventory.getStockQuantity());
        existingInventory.setStockStatus(inventory.getStockStatus());


        return inventoryRepository.save(existingInventory);
    }

    public void deleteInventory(Long id){
        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Inventory not found"));

        inventoryRepository.deleteById(id);
    }
}
