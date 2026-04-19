package com.example.shop.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class VariantRequest {
    private Long id;

    @NotBlank(message = "SKU không được để trống")
    private String sku;

    private String attributes; // Example: {"Color":"Red","Size":"XL"}

    @NotNull(message = "Giá sản phẩm là bắt buộc")
    private BigDecimal price;

    @NotNull(message = "Số lượng là bắt buộc")
    private Integer quantity;

    private Boolean isActive = true;
}
