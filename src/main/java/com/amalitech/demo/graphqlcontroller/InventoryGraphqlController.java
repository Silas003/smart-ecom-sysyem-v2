package com.amalitech.demo.graphqlcontroller;

import com.amalitech.demo.dto.request.InventoryRequest;
import com.amalitech.demo.dto.response.InventoryResponse;
import com.amalitech.demo.services.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@Tag(name = "GraphQL - Inventory", description = "GraphQL queries and mutations for inventory")
public class InventoryGraphqlController {

    private final InventoryService inventoryService;

    public InventoryGraphqlController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @QueryMapping
    @Operation(summary = "List inventories (GraphQL)", description = "List inventories via GraphQL query")
    public List<InventoryResponse> inventories() {
        return inventoryService.getAllInventories();
    }

    @QueryMapping
    @Operation(summary = "Get inventory by id (GraphQL)", description = "Retrieve an inventory by id via GraphQL")
    public InventoryResponse inventoryById(@Argument Long id) {
        return inventoryService.getInventoryById(id);
    }

    @MutationMapping
    @Operation(summary = "Create inventory (GraphQL)", description = "Create a new inventory record via GraphQL mutation")
    public InventoryResponse createInventory(@Argument("input")  InventoryRequest request) {
        return inventoryService.createInventory(request);
    }

    @MutationMapping
    @Operation(summary = "Update inventory (GraphQL)", description = "Update an inventory via GraphQL mutation")
    public InventoryResponse updateInventory(@Argument Long id, @Argument("input")  InventoryRequest request) {
        return inventoryService.updateInventory(id, request);
    }

    @MutationMapping
    @Operation(summary = "Delete inventory (GraphQL)", description = "Delete an inventory by id via GraphQL mutation")
    public Boolean deleteInventory(@Argument Long id) {
        inventoryService.deleteInventory(id);
        return true;
    }
}
