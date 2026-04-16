package com.example.shop.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CategoryRequest {
    @NotBlank(message = "Category name cannot be blank")
    private String name;

    private Long parentId;

    private Boolean isActive = true;

}
