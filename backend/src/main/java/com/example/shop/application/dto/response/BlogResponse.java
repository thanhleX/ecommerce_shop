package com.example.shop.application.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class BlogResponse {
    private Long id;
    private String title;
    private String slug;
    private String content;
    private String thumbnail;
    private Boolean isPublished;
    private Boolean isFeatured;
    private Integer carouselOrder;
    private String authorName;
    private String categoryName;
    private Long blogCategoryId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
