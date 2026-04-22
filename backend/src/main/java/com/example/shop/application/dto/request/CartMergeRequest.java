package com.example.shop.application.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class CartMergeRequest {
    @NotEmpty(message = "Danh sách sản phẩm không thể để trống")
    @Valid
    private List<CartItemRequest> items;

    private boolean combine; // true to add quantities, false to ignore/keep existing
}
