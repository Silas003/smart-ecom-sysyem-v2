package com.amalitech.demo.graphqlcontroller;

import com.amalitech.demo.dto.request.CategoryRequest;
import com.amalitech.demo.models.Category;
import com.amalitech.demo.services.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@Tag(name = "GraphQL - Categories", description = "GraphQL queries and mutations for categories")
public class CategoryGraphqlController {

    private final CategoryService categoryService;

    public CategoryGraphqlController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @QueryMapping
    @Operation(summary = "List categories (GraphQL)", description = "List all categories via GraphQL query")
    public List<Category> categories() {
        return categoryService.getAllCategories();
    }

    @QueryMapping
    @Operation(summary = "Get category by id (GraphQL)", description = "Retrieve a single category by id via GraphQL")
    public Category categoryById(@Argument Long id) {
        return categoryService.getCategoryById(id);
    }

    @MutationMapping
    @Operation(summary = "Create category (GraphQL)", description = "Create a new category via GraphQL mutation")
    public Category createCategory(@Argument("input") CategoryRequest request) {
        return categoryService.createCategory(request);
    }

    @MutationMapping
    @Operation(summary = "Update category (GraphQL)", description = "Update a category by id via GraphQL mutation")
    public Category updateCategory(@Argument Long id,@Argument("input")  CategoryRequest categoryRequest) {
        return categoryService.updateCategory(id, categoryRequest);
    }

    @MutationMapping
    @Operation(summary = "Delete category (GraphQL)", description = "Delete a category by id via GraphQL mutation")
    public Boolean deleteCategory(@Argument Long id) {
        categoryService.deleteCategory(id);
        return true;
    }
}
