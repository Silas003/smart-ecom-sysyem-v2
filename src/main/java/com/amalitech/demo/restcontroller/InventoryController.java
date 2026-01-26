package com.amalitech.demo.restcontroller;

import com.amalitech.demo.dto.InventoryResponse;
import com.amalitech.demo.dto.ResponseDto;
import com.amalitech.demo.services.InventoryService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/inventories")
@AllArgsConstructor
public class InventoryController {
    private final InventoryService inventoryService;

    @GetMapping("/")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto<List<InventoryResponse>> getAllInventories(){
        List<InventoryResponse> inventories = inventoryService.getAllInventories();
        return new ResponseDto<>(HttpStatus.OK,"inventories retrieved",inventories);


    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto<InventoryResponse> getInventoryById(@PathVariable Long id){
        InventoryResponse inventory = inventoryService.getInventoryById(id);
        return new ResponseDto<>(HttpStatus.OK,"inventory retrieved",inventory);

    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto<InventoryResponse> updateInventory(@PathVariable Long id, @RequestBody @Valid com.amalitech.demo.dto.InventoryRequest inventoryRequest){
        InventoryResponse updatedInventory = inventoryService.updateInventory(id, inventoryRequest);
        return new ResponseDto<>(HttpStatus.ACCEPTED,"inventory updated",updatedInventory);

    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteInventory(@PathVariable Long id) {
        inventoryService.deleteInventory(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/create_inventory")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseDto<InventoryResponse> createInventory(@RequestBody @Valid com.amalitech.demo.dto.InventoryRequest inventoryRequest) {
        InventoryResponse newInventory = inventoryService.createInventory(inventoryRequest);
        return new ResponseDto<>(HttpStatus.CREATED,"inventory created",newInventory);

    }

}
