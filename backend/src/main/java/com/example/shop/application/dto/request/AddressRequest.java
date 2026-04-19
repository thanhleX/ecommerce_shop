package com.example.shop.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddressRequest {
    @NotBlank(message = "Địa chỉ chi tiết không được để trống")
    private String fullAddress;

    @NotBlank(message = "Số điện thoại không được để trống")
    private String phone;

    @NotBlank(message = "Tên người nhận không được để trống")
    private String receiverName;

    private Boolean isDefault;
}
