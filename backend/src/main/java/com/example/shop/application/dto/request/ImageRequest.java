package com.example.shop.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ImageRequest {
    @NotBlank(message = "Image URL is required")
    private String imageUrl;

    private Boolean isThumbnail = false;

    private Integer sortOrder = 0;
}
