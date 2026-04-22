package com.example.shop.application.service;

import com.example.shop.application.dto.request.CartItemRequest;
import com.example.shop.application.dto.response.CartItemResponse;
import com.example.shop.application.dto.response.CartResponse;
import com.example.shop.application.mapper.CartMapper;
import com.example.shop.domain.entity.Cart;
import com.example.shop.domain.entity.CartItem;
import com.example.shop.domain.entity.ProductVariant;
import com.example.shop.domain.exception.AppException;
import com.example.shop.domain.exception.ErrorCode;
import com.example.shop.domain.repository.CartItemRepository;
import com.example.shop.domain.repository.CartRepository;
import com.example.shop.domain.repository.ProductVariantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductVariantRepository productVariantRepository;
    private final CartMapper cartMapper;

    @Transactional(readOnly = true)
    public CartResponse getCart(Long userId) {
        Cart cart = getCartEntity(userId);
        return buildCartResponse(cart);
    }

    @Transactional
    public CartResponse addItem(Long userId, CartItemRequest request) {
        Cart cart = getCartEntity(userId);
        ProductVariant variant = productVariantRepository.findById(request.getProductVariantId())
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_VARIANT_NOT_FOUND));

        if (variant.getQuantity() < request.getQuantity()) {
            throw new AppException(ErrorCode.INSUFFICIENT_STOCK);
        }

        CartItem cartItem = cartItemRepository.findByCartAndProductVariant(cart, variant)
                .orElse(CartItem.builder()
                        .cart(cart)
                        .productVariant(variant)
                        .quantity(0)
                        .build());

        cartItem.setQuantity(cartItem.getQuantity() + request.getQuantity());
        
        if (variant.getQuantity() < cartItem.getQuantity()) {
            throw new AppException(ErrorCode.INSUFFICIENT_STOCK);
        }

        cartItemRepository.save(cartItem);
        return buildCartResponse(cart);
    }

    @Transactional
    public CartResponse updateItemQuantity(Long userId, Long cartItemId, Integer quantity) {
        Cart cart = getCartEntity(userId);
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new AppException(ErrorCode.CART_ITEM_NOT_FOUND));

        if (!cartItem.getCart().getId().equals(cart.getId())) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }

        if (quantity <= 0) {
            cartItemRepository.delete(cartItem);
        } else {
            if (cartItem.getProductVariant().getQuantity() < quantity) {
                throw new AppException(ErrorCode.INSUFFICIENT_STOCK);
            }
            cartItem.setQuantity(quantity);
            cartItemRepository.save(cartItem);
        }

        return buildCartResponse(cart);
    }

    @Transactional
    public CartResponse removeItem(Long userId, Long cartItemId) {
        Cart cart = getCartEntity(userId);
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new AppException(ErrorCode.CART_ITEM_NOT_FOUND));

        if (!cartItem.getCart().getId().equals(cart.getId())) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }

        cartItemRepository.delete(cartItem);
        return buildCartResponse(cart);
    }

    @Transactional
    public CartResponse clearCart(Long userId) {
        Cart cart = getCartEntity(userId);
        List<CartItem> items = cartItemRepository.findByCart(cart);
        cartItemRepository.deleteAll(items);
        return buildCartResponse(cart);
    }

    @Transactional
    public CartResponse mergeCart(Long userId, List<CartItemRequest> items, boolean combine) {
        Cart cart = getCartEntity(userId);
        
        for (CartItemRequest itemRequest : items) {
            ProductVariant variant = productVariantRepository.findById(itemRequest.getProductVariantId())
                    .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_VARIANT_NOT_FOUND));

            CartItem existingItem = cartItemRepository.findByCartAndProductVariant(cart, variant)
                    .orElse(null);

            if (existingItem != null) {
                if (combine) {
                    existingItem.setQuantity(existingItem.getQuantity() + itemRequest.getQuantity());
                    // Check stock after combining
                    if (variant.getQuantity() < existingItem.getQuantity()) {
                        existingItem.setQuantity(variant.getQuantity()); // Cap at max available
                    }
                    cartItemRepository.save(existingItem);
                }
                // If not combine, we do nothing (keep existing item in DB as requested "không thì xóa" - usually means don't add the guest one)
            } else {
                // New item from local cart
                CartItem newItem = CartItem.builder()
                        .cart(cart)
                        .productVariant(variant)
                        .quantity(Math.min(itemRequest.getQuantity(), variant.getQuantity()))
                        .build();
                cartItemRepository.save(newItem);
            }
        }
        
        return buildCartResponse(cart);
    }

    public Cart getCartEntity(Long userId) {
        return cartRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException(ErrorCode.CART_NOT_FOUND));
    }

    private CartResponse buildCartResponse(Cart cart) {
        List<CartItem> items = cartItemRepository.findByCart(cart);
        
        List<CartItemResponse> itemResponses = items.stream()
                .map(cartMapper::toCartItemResponse)
                .toList();

        BigDecimal totalPrice = itemResponses.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return cartMapper.toCartResponse(cart, itemResponses, totalPrice);
    }
}
