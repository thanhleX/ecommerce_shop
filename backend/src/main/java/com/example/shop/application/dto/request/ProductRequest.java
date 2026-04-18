package com.example.shop.application.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class ProductRequest {
    @NotBlank(message = "Product name is required")
    private String name;

    private String description;

    @NotNull(message = "Category ID is required")
    private Long categoryId;

    private Boolean isActive = true;

    @Valid
    private List<VariantRequest> variants;

    // Fields for Simple Product (No variants)
    private String sku;
    private java.math.BigDecimal price;
    private Integer quantity;

    @Valid
    private List<ImageRequest> images;
}
