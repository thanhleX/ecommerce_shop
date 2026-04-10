package com.example.shop.application.dto.response;

import com.example.shop.domain.enums.VoucherStatus;
import com.example.shop.domain.enums.VoucherType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class VoucherResponse {
    private Long id;
    private String code;
    private VoucherType type;
    private Double value;
    private Double maxDiscount;
    private Double minOrderValue;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer usageLimit;
    private Integer usagePerUser;
    private VoucherStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
