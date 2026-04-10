package com.example.shop.application.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ImageResponse {
    private Long id;
    private String imageUrl;
    private Boolean isThumbnail;
    private Integer sortOrder;
}
