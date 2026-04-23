package com.example.shop.api.controller;

import com.example.shop.application.dto.common.ApiResponse;
import com.example.shop.application.dto.common.PageResponse;
import com.example.shop.application.dto.response.BlogCategoryResponse;
import com.example.shop.application.dto.response.BlogResponse;
import com.example.shop.application.service.BlogCategoryService;
import com.example.shop.application.service.BlogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Blog", description = "Blog public APIs")
public class BlogController {

    private final BlogService blogService;
    private final BlogCategoryService blogCategoryService;

    @GetMapping("/api/blogs")
    @Operation(summary = "Get published blogs (Public)")
    public ResponseEntity<ApiResponse<PageResponse<BlogResponse>>> getPublishedBlogs(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(PageResponse.of(blogService.getPublishedBlogs(pageable)), "Lấy danh sách bài viết thành công"));
    }

    @GetMapping("/api/blogs/featured")
    @Operation(summary = "Get featured blogs (Public)")
    public ResponseEntity<ApiResponse<List<BlogResponse>>> getFeaturedBlogs() {
        return ResponseEntity.ok(ApiResponse.success(blogService.getFeaturedBlogs(), "Lấy danh sách bài viết nổi bật thành công"));
    }

    @GetMapping("/api/blogs/{slug}")
    @Operation(summary = "Get blog by slug (Public)")
    public ResponseEntity<ApiResponse<BlogResponse>> getBlogBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(ApiResponse.success(blogService.getBlogBySlug(slug), "Lấy nội dung bài viết thành công"));
    }

    @GetMapping("/api/blog-categories")
    @Operation(summary = "Get active blog categories (Public)")
    public ResponseEntity<ApiResponse<List<BlogCategoryResponse>>> getCategories() {
        return ResponseEntity.ok(ApiResponse.success(blogCategoryService.getActiveCategories(), "Lấy danh mục bài viết thành công"));
    }

    @GetMapping("/api/blogs/category/{slug}")
    @Operation(summary = "Get blogs by category slug (Public)")
    public ResponseEntity<ApiResponse<PageResponse<BlogResponse>>> getBlogsByCategory(@PathVariable String slug, Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(PageResponse.of(blogService.getBlogsByCategorySlug(slug, pageable)), "Lấy danh sách bài viết theo danh mục thành công"));
    }
}
