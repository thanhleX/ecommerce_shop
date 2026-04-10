package com.example.shop.domain.repository;

import com.example.shop.domain.entity.CartItem;
import com.example.shop.domain.entity.Cart;
import com.example.shop.domain.entity.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findByCartAndProductVariant(Cart cart, ProductVariant productVariant);
    List<CartItem> findByCart(Cart cart);
}
