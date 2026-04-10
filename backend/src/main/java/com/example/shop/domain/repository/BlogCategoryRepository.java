package com.example.shop.domain.repository;

import com.example.shop.domain.entity.BlogCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BlogCategoryRepository extends JpaRepository<BlogCategory, Long> {
    List<BlogCategory> findByIsActiveTrue();
}
