package com.amalitech.demo.graphqlcontroller;

import com.amalitech.demo.dto.request.InventoryRequest;
import com.amalitech.demo.dto.response.InventoryResponse;
import com.amalitech.demo.models.Inventory;
import com.amalitech.demo.models.Product;
import com.amalitech.demo.services.InventoryService;
import com.amalitech.demo.services.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@Tag(name = "GraphQL - Inventory", description = "GraphQL queries and mutations for inventory")
public class InventoryGraphqlController {

    private final InventoryService inventoryService;
    private final ProductService productService;

    public InventoryGraphqlController(InventoryService inventoryService, ProductService productService) {
        this.inventoryService = inventoryService;
        this.productService = productService;
    }

    @QueryMapping
    @Operation(summary = "List inventories (GraphQL)", description = "List inventories via GraphQL query")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inventories retrieved",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = InventoryResponse.class))))
    })
    public List<InventoryResponse> inventories() {
        return inventoryService.getAllInventories();
    }

    @QueryMapping
    @Operation(summary = "Get inventory by id (GraphQL)", description = "Retrieve an inventory by id via GraphQL")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inventory retrieved",
                    content = @Content(schema = @Schema(implementation = InventoryResponse.class))),
            @ApiResponse(responseCode = "404", description = "Inventory not found")
    })
    public InventoryResponse inventoryById(@Argument Long id) {
        return inventoryService.getInventoryById(id);
    }

    @MutationMapping
    @Operation(summary = "Create inventory (GraphQL)", description = "Create a new inventory record via GraphQL mutation")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Inventory created",
                    content = @Content(schema = @Schema(implementation = InventoryResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation error")
    })
    public InventoryResponse createInventory(@Argument("input")  InventoryRequest request) {
        return inventoryService.createInventory(request);
    }

    @MutationMapping
    @Operation(summary = "Update inventory (GraphQL)", description = "Update an inventory via GraphQL mutation")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inventory updated",
                    content = @Content(schema = @Schema(implementation = InventoryResponse.class))),
            @ApiResponse(responseCode = "404", description = "Inventory not found")
    })
    public InventoryResponse updateInventory(@Argument Long id, @Argument("input")  InventoryRequest request) {
        return inventoryService.updateInventory(id, request);
    }

    @MutationMapping
    @Operation(summary = "Delete inventory (GraphQL)", description = "Delete an inventory by id via GraphQL mutation")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inventory deleted"),
            @ApiResponse(responseCode = "404", description = "Inventory not found")
    })
    public Boolean deleteInventory(@Argument Long id) {
        inventoryService.deleteInventory(id);
        return true;
    }


    @SchemaMapping(typeName = "Inventory", field = "product")
    public Product product(Inventory inventory) {
        return productService.getProductById(inventory.getProduct().getId());
    }
}
