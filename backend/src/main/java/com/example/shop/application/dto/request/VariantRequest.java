package com.example.shop.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class VariantRequest {
    private Long id;

    @NotBlank(message = "SKU cannot be blank")
    private String sku;

    private String attributes; // Example: {"Color":"Red","Size":"XL"}

    @NotNull(message = "Price is required")
    private BigDecimal price;

    @NotNull(message = "Quantity is required")
    private Integer quantity;

    private Boolean isActive = true;
}
