package com.example.shop.application.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PlaceOrderRequest {

    @NotNull(message = "ID phương thức thanh toán là bắt buộc")
    private Long paymentMethodId;

    @NotNull(message = "ID địa chỉ là bắt buộc")
    private Long addressId;

    private String note;
    private String voucherCode;
}
