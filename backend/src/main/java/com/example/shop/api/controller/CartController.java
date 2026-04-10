package com.example.shop.api.controller;

import com.example.shop.application.dto.request.CartItemRequest;
import com.example.shop.application.dto.common.ApiResponse;
import com.example.shop.application.dto.response.CartResponse;
import com.example.shop.application.service.CartService;
import com.example.shop.infrastructure.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@Tag(name = "Cart", description = "Cart management APIs")
@PreAuthorize("hasRole('CUSTOMER')")
public class CartController {

    private final CartService cartService;

    @GetMapping
    @Operation(summary = "Get current user's cart")
    public ResponseEntity<ApiResponse<CartResponse>> getCart(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        CartResponse cartResponse = cartService.getCart(userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success(cartResponse));
    }

    @PostMapping("/items")
    @Operation(summary = "Add item to cart")
    public ResponseEntity<ApiResponse<CartResponse>> addItem(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody CartItemRequest request) {
        CartResponse cartResponse = cartService.addItem(userDetails.getId(), request);
        return ResponseEntity.ok(ApiResponse.success(cartResponse));
    }

    @PutMapping("/items/{itemId}")
    @Operation(summary = "Update cart item quantity")
    public ResponseEntity<ApiResponse<CartResponse>> updateItemQuantity(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long itemId,
            @RequestParam Integer quantity) {
        CartResponse cartResponse = cartService.updateItemQuantity(userDetails.getId(), itemId, quantity);
        return ResponseEntity.ok(ApiResponse.success(cartResponse));
    }

    @DeleteMapping("/items/{itemId}")
    @Operation(summary = "Remove item from cart")
    public ResponseEntity<ApiResponse<CartResponse>> removeItem(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long itemId) {
        CartResponse cartResponse = cartService.removeItem(userDetails.getId(), itemId);
        return ResponseEntity.ok(ApiResponse.success(cartResponse));
    }

    @DeleteMapping
    @Operation(summary = "Clear cart")
    public ResponseEntity<ApiResponse<CartResponse>> clearCart(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        CartResponse cartResponse = cartService.clearCart(userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success(cartResponse));
    }
}
