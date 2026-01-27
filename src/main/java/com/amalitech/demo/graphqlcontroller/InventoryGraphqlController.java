package com.amalitech.demo.graphqlcontroller;

import com.amalitech.demo.dto.request.InventoryRequest;
import com.amalitech.demo.dto.response.InventoryResponse;
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
    public List<InventoryResponse> inventories() {
        return inventoryService.getAllInventories();
    }

    @QueryMapping
    public InventoryResponse inventoryById(@Argument Long id) {
        return inventoryService.getInventoryById(id);
    }

    @MutationMapping
    public InventoryResponse createInventory(@Argument("input")  InventoryRequest request) {
        return inventoryService.createInventory(request);
    }

    @MutationMapping
    public InventoryResponse updateInventory(@Argument Long id, @Argument("input")  InventoryRequest request) {
        return inventoryService.updateInventory(id, request);
    }

    @MutationMapping
    public Boolean deleteInventory(@Argument Long id) {
        inventoryService.deleteInventory(id);
        return true;
    }
}
