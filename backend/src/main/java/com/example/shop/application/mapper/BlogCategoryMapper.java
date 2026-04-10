package com.example.shop.application.mapper;

import com.example.shop.application.dto.request.BlogCategoryRequest;
import com.example.shop.application.dto.response.BlogCategoryResponse;
import com.example.shop.domain.entity.BlogCategory;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BlogCategoryMapper {
    BlogCategory toEntity(BlogCategoryRequest request);
    BlogCategoryResponse toResponse(BlogCategory blogCategory);
}
