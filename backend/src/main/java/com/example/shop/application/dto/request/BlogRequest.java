package com.example.shop.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BlogRequest {

    @NotBlank(message = "Tiêu đề không được để trống")
    private String title;

    @NotBlank(message = "Slug không được để trống")
    private String slug;

    private String content;
    private String thumbnail;
    private Boolean isPublished = false;
    private Boolean isFeatured = false;
    private Integer carouselOrder;
    private Long blogCategoryId;
}
