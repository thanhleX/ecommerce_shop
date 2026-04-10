package com.example.shop.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddressRequest {
    @NotBlank(message = "Full address cannot be blank")
    private String fullAddress;

    @NotBlank(message = "Phone cannot be blank")
    private String phone;

    @NotBlank(message = "Receiver name cannot be blank")
    private String receiverName;

    private Boolean isDefault;
}
