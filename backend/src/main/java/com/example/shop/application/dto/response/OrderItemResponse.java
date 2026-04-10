package com.example.shop.application.dto.response;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class OrderItemResponse {
    private Long id;
    private Long productVariantId;
    private String productName;
    private String variantAttributes;
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal totalAmount;
    private String imageUrl;
    private String sku;
}
