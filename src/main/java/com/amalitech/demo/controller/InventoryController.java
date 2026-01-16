package com.amalitech.demo.controller;

import com.amalitech.demo.models.Inventory;
import com.amalitech.demo.services.InventoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/inventories")
public class InventoryController {
    private static InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }
    @GetMapping("/")
    public ResponseEntity<List<Inventory>> getAllInventorys(){
        List<Inventory> inventory = inventoryService.getAllInventories();
        return  ResponseEntity.status(HttpStatus.OK).body(inventory);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Inventory> getInventoryById(@PathVariable Long id){
        Inventory inventory = inventoryService.getInventoryById(id);
        return  ResponseEntity.status(HttpStatus.OK).body(inventory);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Inventory> updateInventory(@PathVariable Long id, @RequestBody @Valid Inventory inventory){
        Inventory updatedInventory = inventoryService.updateInventory(id, inventory);
        return  ResponseEntity.status(HttpStatus.ACCEPTED).body(updatedInventory);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInventory(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping("/create_inventory")
    public ResponseEntity<Inventory> createInventory(@RequestBody @Valid Inventory inventory) {
        Inventory newInventory = inventoryService.createInventory(inventory);
        return ResponseEntity.status(HttpStatus.CREATED).body(newInventory);
    }

}
