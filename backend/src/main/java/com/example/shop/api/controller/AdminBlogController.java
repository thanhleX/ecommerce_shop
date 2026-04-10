package com.example.shop.api.controller;

import com.example.shop.application.dto.common.ApiResponse;
import com.example.shop.application.dto.common.PageResponse;
import com.example.shop.application.dto.request.BlogCategoryRequest;
import com.example.shop.application.dto.request.BlogRequest;
import com.example.shop.application.dto.response.BlogCategoryResponse;
import com.example.shop.application.dto.response.BlogResponse;
import com.example.shop.application.service.BlogCategoryService;
import com.example.shop.application.service.BlogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Admin Blog", description = "Admin Blog management APIs")
public class AdminBlogController {

    private final BlogService blogService;
    private final BlogCategoryService blogCategoryService;

    // ==================== Blog ====================

    @GetMapping("/api/admin/blogs")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all blogs (Admin)")
    public ResponseEntity<ApiResponse<PageResponse<BlogResponse>>> getAllBlogs(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(PageResponse.of(blogService.getAllBlogs(pageable))));
    }

    @PostMapping("/api/admin/blogs")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a blog (Admin)")
    public ResponseEntity<ApiResponse<BlogResponse>> createBlog(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody @Valid BlogRequest request) {
        return ResponseEntity.ok(ApiResponse.success(blogService.create(userDetails.getUsername(), request)));
    }

    @PutMapping("/api/admin/blogs/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update a blog (Admin)")
    public ResponseEntity<ApiResponse<BlogResponse>> updateBlog(
            @PathVariable Long id,
            @RequestBody @Valid BlogRequest request) {
        return ResponseEntity.ok(ApiResponse.success(blogService.update(id, request)));
    }

    @DeleteMapping("/api/admin/blogs/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete a blog (Admin)")
    public ResponseEntity<ApiResponse<Void>> deleteBlog(@PathVariable Long id) {
        blogService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    // ==================== Blog Category ====================

    @GetMapping("/api/admin/blog-categories")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all blog categories (Admin)")
    public ResponseEntity<ApiResponse<List<BlogCategoryResponse>>> getAllCategories() {
        return ResponseEntity.ok(ApiResponse.success(blogCategoryService.getAll()));
    }

    @PostMapping("/api/admin/blog-categories")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a blog category (Admin)")
    public ResponseEntity<ApiResponse<BlogCategoryResponse>> createCategory(
            @RequestBody @Valid BlogCategoryRequest request) {
        return ResponseEntity.ok(ApiResponse.success(blogCategoryService.create(request)));
    }

    @PutMapping("/api/admin/blog-categories/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update a blog category (Admin)")
    public ResponseEntity<ApiResponse<BlogCategoryResponse>> updateCategory(
            @PathVariable Long id,
            @RequestBody @Valid BlogCategoryRequest request) {
        return ResponseEntity.ok(ApiResponse.success(blogCategoryService.update(id, request)));
    }

    @DeleteMapping("/api/admin/blog-categories/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete a blog category (Admin)")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable Long id) {
        blogCategoryService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
