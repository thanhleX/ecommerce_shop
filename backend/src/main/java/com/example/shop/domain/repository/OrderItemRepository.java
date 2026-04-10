package com.example.shop.domain.repository;

import com.example.shop.domain.entity.OrderItem;
import com.example.shop.domain.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findByOrder(Order order);
}
