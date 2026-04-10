package com.example.shop.domain.entity;

import com.example.shop.domain.enums.VoucherStatus;
import com.example.shop.domain.enums.VoucherType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "vouchers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Voucher extends BaseAuditEntity {

    @Column(unique = true, nullable = false, length = 50)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VoucherType type;

    @Column(nullable = false)
    private Double value;

    @Column(name = "max_discount")
    private Double maxDiscount;

    @Column(name = "min_order_value")
    private Double minOrderValue;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "usage_limit")
    private Integer usageLimit;

    @Column(name = "usage_per_user")
    private Integer usagePerUser;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VoucherStatus status;

    @Override
    protected void onBaseCreate() {
        super.onBaseCreate();
        if (status == null) status = VoucherStatus.ACTIVE;
        if (usagePerUser == null) usagePerUser = 1;
    }
}
