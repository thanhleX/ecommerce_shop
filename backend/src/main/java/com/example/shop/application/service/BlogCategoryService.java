package com.example.shop.application.service;

import com.example.shop.application.dto.request.BlogCategoryRequest;
import com.example.shop.application.dto.response.BlogCategoryResponse;
import com.example.shop.application.mapper.BlogCategoryMapper;
import com.example.shop.domain.entity.BlogCategory;
import com.example.shop.domain.exception.AppException;
import com.example.shop.domain.exception.ErrorCode;
import com.example.shop.domain.repository.BlogCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BlogCategoryService {

    private final BlogCategoryRepository blogCategoryRepository;
    private final BlogCategoryMapper blogCategoryMapper;

    public List<BlogCategoryResponse> getActiveCategories() {
        return blogCategoryRepository.findByIsActiveTrue()
                .stream()
                .map(blogCategoryMapper::toResponse)
                .toList();
    }

    public List<BlogCategoryResponse> getAll() {
        return blogCategoryRepository.findAll()
                .stream()
                .map(blogCategoryMapper::toResponse)
                .toList();
    }

    @Transactional
    public BlogCategoryResponse create(BlogCategoryRequest request) {
        BlogCategory category = blogCategoryMapper.toEntity(request);
        return blogCategoryMapper.toResponse(blogCategoryRepository.save(category));
    }

    @Transactional
    public BlogCategoryResponse update(Long id, BlogCategoryRequest request) {
        BlogCategory category = blogCategoryRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.BLOG_CATEGORY_NOT_FOUND));
        category.setName(request.getName());
        category.setSlug(request.getSlug());
        category.setIsActive(request.getIsActive());
        return blogCategoryMapper.toResponse(blogCategoryRepository.save(category));
    }

    @Transactional
    public void delete(Long id) {
        BlogCategory category = blogCategoryRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.BLOG_CATEGORY_NOT_FOUND));
        category.setIsActive(false);
        blogCategoryRepository.save(category);
    }
}
