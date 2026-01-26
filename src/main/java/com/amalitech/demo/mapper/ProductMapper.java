package com.amalitech.demo.mapper;

import com.amalitech.demo.dto.ProductRequest;
import com.amalitech.demo.dto.ProductResponse;
import com.amalitech.demo.models.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    // category will be set in the service after resolving the id
    Product toEntity(ProductRequest req);

    @Mapping(source = "category.id", target = "categoryId")
    ProductResponse toResponse(Product product);
}
