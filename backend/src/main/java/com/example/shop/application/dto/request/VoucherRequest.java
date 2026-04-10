package com.example.shop.application.dto.request;

import com.example.shop.domain.enums.VoucherStatus;
import com.example.shop.domain.enums.VoucherType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class VoucherRequest {

    @NotBlank(message = "Mã voucher không được để trống")
    private String code;

    @NotNull(message = "Loại voucher không được để trống")
    private VoucherType type;

    @NotNull(message = "Giá trị giảm không được để trống")
    private Double value;

    private Double maxDiscount;
    private Double minOrderValue;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer usageLimit;
    private Integer usagePerUser;
    private VoucherStatus status;
}
