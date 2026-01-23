package com.amalitech.demo.mapper;

import com.amalitech.demo.dto.ProductRequest;
import com.amalitech.demo.models.Product;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    // category will be set in the service after resolving the id
    Product toEntity(ProductRequest req);
}
