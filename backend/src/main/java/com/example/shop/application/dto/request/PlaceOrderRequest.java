package com.example.shop.application.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PlaceOrderRequest {

    @NotNull(message = "Payment method ID is required")
    private Long paymentMethodId;

    @NotNull(message = "Address ID is required")
    private Long addressId;

    private String note;
    private String voucherCode;
}
