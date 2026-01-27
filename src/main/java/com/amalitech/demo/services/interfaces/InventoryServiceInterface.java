package com.amalitech.demo.services.interfaces;

import com.amalitech.demo.dto.request.InventoryRequest;
import com.amalitech.demo.dto.response.InventoryResponse;

import java.util.List;

public interface InventoryServiceInterface {
    InventoryResponse createInventory(InventoryRequest request);

    InventoryResponse getInventoryById(Long id);

    List<InventoryResponse> getAllInventories();

    InventoryResponse updateInventory(Long id, InventoryRequest request);

    void deleteInventory(Long id);
}
