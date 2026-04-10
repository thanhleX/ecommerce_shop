package com.example.shop.application.mapper;

import com.example.shop.application.dto.request.BlogRequest;
import com.example.shop.application.dto.response.BlogResponse;
import com.example.shop.domain.entity.Blog;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface BlogMapper {

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "blogCategory", ignore = true)
    Blog toEntity(BlogRequest request);

    @Mapping(source = "user.fullName", target = "authorName")
    @Mapping(source = "blogCategory.name", target = "categoryName")
    @Mapping(source = "blogCategory.id", target = "blogCategoryId")
    BlogResponse toResponse(Blog blog);
}
