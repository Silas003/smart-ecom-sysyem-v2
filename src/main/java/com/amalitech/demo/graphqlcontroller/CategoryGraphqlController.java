package com.amalitech.demo.graphqlcontroller;

import com.amalitech.demo.dto.CategoryInput;
import com.amalitech.demo.dto.CategoryRequest;
import com.amalitech.demo.models.Category;
import com.amalitech.demo.services.CategoryService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class CategoryGraphqlController {

    private final CategoryService categoryService;

    public CategoryGraphqlController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @QueryMapping
    public List<Category> categories() {
        return categoryService.getAllCategories();
    }

    @QueryMapping
    public Category categoryById(@Argument Long id) {
        return categoryService.getCategoryById(id);
    }

    @MutationMapping
    public Category createCategory(@Argument CategoryInput input) {
        CategoryRequest req = new CategoryRequest();
        req.setName(input.getName());
        return categoryService.createCategory(req);
    }

    @MutationMapping
    public Category updateCategory(@Argument Long id, @Argument CategoryInput input) {
        CategoryRequest req = new CategoryRequest();
        req.setName(input.getName());
        return categoryService.updateCategory(id, req);
    }

    @MutationMapping
    public Boolean deleteCategory(@Argument Long id) {
        categoryService.deleteCategory(id);
        return true;
    }
}
