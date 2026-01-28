package com.amalitech.demo.restcontroller;

import com.amalitech.demo.dto.request.CategoryRequest;
import com.amalitech.demo.dto.ResponseDto;
import com.amalitech.demo.dto.response.CategoryResponse;
import com.amalitech.demo.models.Category;
import com.amalitech.demo.services.interfaces.CategoryServiceInterface;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(value = "/api/v1/categories")
@Tag(name = "Categories", description = "Endpoints to manage product categories")
public class CategoryController {
    private final CategoryServiceInterface categoryService;

    @GetMapping("/")
    @Operation(summary = "Get all categories", description = "Retrieve all categories")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categories retrieved",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = Category.class))))
    })
    public ResponseDto<List<CategoryResponse>> getAllCategories(){
        List<CategoryResponse> categories = categoryService.getAllCategories();
        return new ResponseDto<List<Category>>(HttpStatus.OK,"categories retrieved",categories);

    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get category", description = "Retrieve a category by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category retrieved",
                    content = @Content(schema = @Schema(implementation = Category.class))),
            @ApiResponse(responseCode = "404", description = "Category not found")
    })
    public ResponseDto<CategoryResponse> getCategoryById(@Parameter(description = "ID of the category to retrieve", required = true) @PathVariable Long id){
        CategoryResponse category = categoryService.getCategoryById(id);
        return new ResponseDto<>(HttpStatus.OK,"category retrieved",category);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update category", description = "Update a category by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category updated",
                    content = @Content(schema = @Schema(implementation = Category.class))),
            @ApiResponse(responseCode = "404", description = "Category not found"),
            @ApiResponse(responseCode = "400", description = "Validation error")
    })
    public ResponseDto<CategoryResponse> updateCategory(@Parameter(description = "ID of the category to update", required = true) @PathVariable Long id, @RequestBody @Valid CategoryRequest categoryRequest){
        CategoryResponse updatedCategory = categoryService.updateCategory(id, categoryRequest);
        return new ResponseDto<>(HttpStatus.OK,"category updated",updatedCategory);

    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete category", description = "Delete a category by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Category deleted"),
            @ApiResponse(responseCode = "404", description = "Category not found")
    })
    public ResponseEntity<Void> deleteCategory(@Parameter(description = "ID of the category to delete", required = true) @PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping("/create_category")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create category", description = "Create a category")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Category created",
                    content = @Content(schema = @Schema(implementation = Category.class))),
            @ApiResponse(responseCode = "400", description = "Validation error")
    })
    public ResponseDto<CategoryResponse> createCategory(@RequestBody @Valid CategoryRequest categoryRequest) {
        CategoryResponse newCategory = categoryService.createCategory(categoryRequest);
        return new ResponseDto<>(HttpStatus.CREATED,"category created",newCategory);

    }
}
