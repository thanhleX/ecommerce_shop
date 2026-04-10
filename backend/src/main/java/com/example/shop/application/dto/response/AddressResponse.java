package com.example.shop.application.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AddressResponse {
    private Long id;
    private String fullAddress;
    private String phone;
    private String receiverName;
    private Boolean isDefault;
}
