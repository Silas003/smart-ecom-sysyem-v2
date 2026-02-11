package com.amalitech.demo.restcontroller;

import com.amalitech.demo.dto.ResponseDto;
import com.amalitech.demo.dto.request.InventoryRequest;
import com.amalitech.demo.dto.response.InventoryResponse;
import com.amalitech.demo.services.interfaces.InventoryServiceInterface;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/inventories")
@AllArgsConstructor
@Tag(name = "Inventory", description = "Inventory management endpoints")
public class InventoryController {
    private final InventoryServiceInterface  inventoryService;

    @GetMapping("/")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get all inventories", description = "Retrieve all inventory records")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inventories retrieved",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = InventoryResponse.class))))
    })
    public ResponseDto<List<InventoryResponse>> getAllInventories(){
        List<InventoryResponse> inventories = inventoryService.getAllInventories();
        return new ResponseDto<>(HttpStatus.OK,"inventories retrieved",inventories);


    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get inventory", description = "Retrieve a single inventory record by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inventory retrieved",
                    content = @Content(schema = @Schema(implementation = InventoryResponse.class))),
            @ApiResponse(responseCode = "404", description = "Inventory not found")
    })
    public ResponseDto<InventoryResponse> getInventoryById(@Parameter(description = "ID of the inventory to retrieve", required = true) @PathVariable Long id){
        InventoryResponse inventory = inventoryService.getInventoryById(id);
        return new ResponseDto<>(HttpStatus.OK,"inventory retrieved",inventory);

    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update inventory", description = "Update inventory record by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inventory updated",
                    content = @Content(schema = @Schema(implementation = InventoryResponse.class))),
            @ApiResponse(responseCode = "404", description = "Inventory not found"),
            @ApiResponse(responseCode = "400", description = "Validation error")
    })
    public ResponseDto<InventoryResponse> updateInventory(@Parameter(description = "ID of the inventory to update", required = true) @PathVariable Long id, @RequestBody @Valid InventoryRequest inventoryRequest){
        InventoryResponse updatedInventory = inventoryService.updateInventory(id, inventoryRequest);
        return new ResponseDto<>(HttpStatus.ACCEPTED,"inventory updated",updatedInventory);

    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete inventory", description = "Delete inventory by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Inventory deleted"),
            @ApiResponse(responseCode = "404", description = "Inventory not found")
    })
    public ResponseEntity<Void> deleteInventory(@Parameter(description = "ID of the inventory to delete", required = true) @PathVariable Long id) {
        inventoryService.deleteInventory(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/create_inventory")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create inventory", description = "Create a new inventory record")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Inventory created",
                    content = @Content(schema = @Schema(implementation = InventoryResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation error")
    })
    public ResponseDto<InventoryResponse> createInventory(@RequestBody @Valid InventoryRequest inventoryRequest) {
        InventoryResponse newInventory = inventoryService.createInventory(inventoryRequest);
        return new ResponseDto<>(HttpStatus.CREATED,"inventory created",newInventory);

    }

}
