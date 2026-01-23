package com.amalitech.demo.graphqlcontroller;

import com.amalitech.demo.dto.InventoryInput;
import com.amalitech.demo.dto.InventoryRequest;
import com.amalitech.demo.models.Inventory;
import com.amalitech.demo.services.InventoryService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class InventoryGraphqlController {

    private final InventoryService inventoryService;

    public InventoryGraphqlController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @QueryMapping
    public List<Inventory> inventories() {
        return inventoryService.getAllInventories();
    }

    @QueryMapping
    public Inventory inventoryById(@Argument Long id) {
        return inventoryService.getInventoryById(id);
    }

    @MutationMapping
    public Inventory createInventory(@Argument InventoryInput input) {
        InventoryRequest req = new InventoryRequest();
        req.setProductId(input.getProductId());
        req.setStockQuantity(input.getStockQuantity());
        req.setReservedQuantity(input.getReservedQuantity());
        req.setStockStatus(input.getStockStatus());
        return inventoryService.createInventory(req);
    }

    @MutationMapping
    public Inventory updateInventory(@Argument Long id, @Argument InventoryInput input) {
        InventoryRequest req = new InventoryRequest();
        req.setProductId(input.getProductId());
        req.setStockQuantity(input.getStockQuantity());
        req.setReservedQuantity(input.getReservedQuantity());
        req.setStockStatus(input.getStockStatus());
        return inventoryService.updateInventory(id, req);
    }

    @MutationMapping
    public Boolean deleteInventory(@Argument Long id) {
        inventoryService.deleteInventory(id);
        return true;
    }
}
