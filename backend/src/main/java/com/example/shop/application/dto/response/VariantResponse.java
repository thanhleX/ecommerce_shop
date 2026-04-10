package com.example.shop.application.dto.response;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class VariantResponse {
    private Long id;
    private String sku;
    private String attributes;
    private BigDecimal price;
    private Integer quantity;
    private Boolean isActive;
}
