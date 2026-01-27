package com.amalitech.demo.restcontroller;

import com.amalitech.demo.dto.request.CategoryRequest;
import com.amalitech.demo.dto.ResponseDto;
import com.amalitech.demo.models.Category;
import com.amalitech.demo.services.CategoryService;
import com.amalitech.demo.services.interfaces.CategoryServiceInterface;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(value = "/api/v1/categories")
public class CategoryController {
    private final CategoryServiceInterface categoryService;

    @GetMapping("/")
    @Operation(summary = "Get all categories", description = "Retrieve all categories")
    public ResponseDto<List<Category>> getAllCategories(){
        List<Category> categories = categoryService.getAllCategories();
        return new ResponseDto<List<Category>>(HttpStatus.OK,"categories retrieved",categories);

    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get category", description = "Retrieve a category by id")
    public ResponseDto<Category> getCategoryById(@PathVariable Long id){
        Category category = categoryService.getCategoryById(id);
        return new ResponseDto<>(HttpStatus.OK,"category retrieved",category);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update category", description = "Update a category by id")
    public ResponseDto<Category> updateCategory(@PathVariable Long id, @RequestBody @Valid CategoryRequest categoryRequest){
        Category updatedCategory = categoryService.updateCategory(id, categoryRequest);
        return new ResponseDto<>(HttpStatus.OK,"category updated",updatedCategory);

    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete category", description = "Delete a category by id")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping("/create_category")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create category", description = "Create a category ")
    public ResponseDto<Category> createCategory(@RequestBody @Valid CategoryRequest categoryRequest) {
        Category newCategory = categoryService.createCategory(categoryRequest);
        return new ResponseDto<>(HttpStatus.CREATED,"category created",newCategory);

    }
}
