package com.example.shop.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_variant_id")
    private ProductVariant productVariant;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "variant_attributes", columnDefinition = "TEXT")
    private String variantAttributes;

    @Column(precision = 12, scale = 2)
    private BigDecimal price;

    private Integer quantity;

    @Column(name = "total_amount", precision = 12, scale = 2)
    private BigDecimal totalAmount;
}
