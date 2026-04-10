package com.example.shop.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "product_variants")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductVariant extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(columnDefinition = "TEXT")
    private String attributes;

    @Column(unique = true, length = 100)
    private String sku;

    @Column(precision = 12, scale = 2)
    private BigDecimal price;

    private Integer quantity;

    @Column(name = "is_active")
    private Boolean isActive;
}
