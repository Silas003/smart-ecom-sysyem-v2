package com.amalitech.demo.mapper;

import com.amalitech.demo.dto.request.CategoryRequest;
import com.amalitech.demo.dto.response.CategoryResponse;
import com.amalitech.demo.models.Category;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    Category toEntity(CategoryRequest req);

    CategoryResponse toDto(Category entity);
    List<CategoryResponse> toDto(List<Category> list);
}
