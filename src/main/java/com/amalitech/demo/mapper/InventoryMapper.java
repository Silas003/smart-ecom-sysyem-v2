package com.amalitech.demo.mapper;

import com.amalitech.demo.dto.InventoryRequest;
import com.amalitech.demo.models.Inventory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface InventoryMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "product", ignore = true) // product will be set in the service after resolving productId
    Inventory toEntity(InventoryRequest req);
}
