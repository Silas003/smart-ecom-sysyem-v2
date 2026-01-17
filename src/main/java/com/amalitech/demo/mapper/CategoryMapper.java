package com.amalitech.demo.mapper;

import com.amalitech.demo.dto.CategoryRequest;
import com.amalitech.demo.models.Category;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    Category toEntity(CategoryRequest req);
}
