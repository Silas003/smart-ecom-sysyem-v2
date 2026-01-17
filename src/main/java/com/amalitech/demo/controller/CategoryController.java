package com.amalitech.demo.controller;

import com.amalitech.demo.dto.ResponseDto;
import com.amalitech.demo.models.Category;
import com.amalitech.demo.services.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/categories")
public class CategoryController {
    private final CategoryService categoryService;
    public CategoryController(CategoryService categoryService){
        this.categoryService = categoryService;
    }

    @GetMapping("/")
    public ResponseEntity<ResponseDto> getAllCategories(){
        List<Category> categories = categoryService.getAllCategories();
        ResponseDto  responseDto = new ResponseDto<List<Category>>(HttpStatus.OK,"categories retrieved",categories);
        return  ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseDto> getCategoryById(@PathVariable Long id){
        Category category = categoryService.getCategoryById(id);
        ResponseDto  responseDto = new ResponseDto<Category>(HttpStatus.OK,"category retrieved",category);
        return  ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseDto> updateCategory(@PathVariable Long id, @RequestBody @Valid Category category){
        Category updatedCategory = categoryService.updateCategory(id, category);
        ResponseDto  responseDto = new ResponseDto<Category>(HttpStatus.OK,"category retrieved",updatedCategory);
        return  ResponseEntity.status(HttpStatus.ACCEPTED).body(responseDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDto> deleteCategory(@PathVariable Long id) {
        ResponseDto  responseDto = new ResponseDto<Category>(HttpStatus.NO_CONTENT,"category deleted successfully",null);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(responseDto);
    }

    @PostMapping("/create_category")
    public ResponseEntity<ResponseDto> createCategory(@RequestBody @Valid Category category) {
        Category newCategory = categoryService.createCategory(category);
        ResponseDto  responseDto = new ResponseDto<Category>(HttpStatus.CREATED,"category created",newCategory);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }
}
