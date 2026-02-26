package com.amalitech.demo.mapper;

import com.amalitech.demo.dto.request.InventoryRequest;
import com.amalitech.demo.dto.response.InventoryResponse;
import com.amalitech.demo.models.Inventory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface InventoryMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "product", ignore = true)
        @Mapping(target = "version",ignore = true)// product will be set in the service after resolving productId
    Inventory toEntity(InventoryRequest req);

    @Mapping(source = "product.id", target = "productId")
    InventoryResponse toResponse(Inventory inventory);
}
