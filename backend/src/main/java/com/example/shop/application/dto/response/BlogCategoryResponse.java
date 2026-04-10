package com.example.shop.application.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class BlogCategoryResponse {
    private Long id;
    private String name;
    private String slug;
    private Boolean isActive;
}
