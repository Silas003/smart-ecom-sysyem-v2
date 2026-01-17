package com.amalitech.demo.controller;

import com.amalitech.demo.dto.ResponseDto;
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
    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }
    @GetMapping("/")
    public ResponseEntity<ResponseDto> getAllInventorys(){
        List<Inventory> inventories = inventoryService.getAllInventories();
        ResponseDto responseDto = new ResponseDto(HttpStatus.OK,"inventories retrieved",inventories);

        return  ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseDto> getInventoryById(@PathVariable Long id){
        Inventory inventory = inventoryService.getInventoryById(id);
        ResponseDto responseDto = new ResponseDto(HttpStatus.OK,"users retrieved",inventory);

        return  ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseDto> updateInventory(@PathVariable Long id, @RequestBody @Valid Inventory inventory){
        Inventory updatedInventory = inventoryService.updateInventory(id, inventory);
        ResponseDto responseDto = new ResponseDto(HttpStatus.ACCEPTED,"users retrieved",updatedInventory);

        return  ResponseEntity.status(HttpStatus.ACCEPTED).body(responseDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDto> deleteInventory(@PathVariable Long id) {
        inventoryService.deleteInventory(id);
        ResponseDto responseDto = new ResponseDto(HttpStatus.OK,"users retrieved",null);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(responseDto);
    }

    @PostMapping("/create_inventory")
    public ResponseEntity<ResponseDto> createInventory(@RequestBody @Valid Inventory inventory) {
        Inventory newInventory = inventoryService.createInventory(inventory);
        ResponseDto responseDto = new ResponseDto(HttpStatus.OK,"users retrieved",newInventory);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

}
