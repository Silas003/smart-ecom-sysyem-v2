package com.amalitech.demo.services.interfaces;

import com.amalitech.demo.dto.request.InventoryRequest;
import com.amalitech.demo.dto.response.InventoryResponse;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface InventoryServiceInterface {
    InventoryResponse createInventory(InventoryRequest request);

    InventoryResponse getInventoryById(Long id);

    List<InventoryResponse> getAllInventories();

    @Transactional(propagation = Propagation.MANDATORY)
    InventoryResponse updateInventory(Long id, InventoryRequest request);

    void deleteInventory(Long id);
}
