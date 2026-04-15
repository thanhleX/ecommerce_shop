package com.example.shop.api.controller;

import com.example.shop.application.dto.common.ApiResponse;
import com.example.shop.application.dto.request.CategoryRequest;
import com.example.shop.application.dto.response.CategoryResponse;
import com.example.shop.application.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getCategoryTree(
            @RequestParam(required = false) Boolean activeOnly) {
        return ResponseEntity.ok(
                ApiResponse.success(categoryService.getCategoryTree(activeOnly), "Categories retrieved successfully"));
    }

    @GetMapping("/categories/all")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getAllCategories(
            @RequestParam(required = false) Boolean activeOnly) {
        return ResponseEntity.ok(
                ApiResponse.success(categoryService.getAllCategories(activeOnly), "All categories retrieved successfully"));
    }

    @GetMapping("/categories/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> getCategoryById(@PathVariable Long id) {
        return ResponseEntity
                .ok(ApiResponse.success(categoryService.getCategoryById(id), "Category retrieved successfully"));
    }

    @PostMapping("/admin/categories")
    @PreAuthorize("hasAuthority('category:manage')")
    public ResponseEntity<ApiResponse<CategoryResponse>> createCategory(@Valid @RequestBody CategoryRequest request) {
        CategoryResponse response = categoryService.createCategory(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Category created successfully"));
    }

    @PutMapping("/admin/categories/{id}")
    @PreAuthorize("hasAuthority('category:manage')")
    public ResponseEntity<ApiResponse<CategoryResponse>> updateCategory(
            @PathVariable Long id, @Valid @RequestBody CategoryRequest request) {
        CategoryResponse response = categoryService.updateCategory(id, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Category updated successfully"));
    }

    @DeleteMapping("/admin/categories/{id}")
    @PreAuthorize("hasAuthority('category:manage')")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Category deleted successfully"));
    }
}
