package com.example.shop.domain.repository;

import com.example.shop.domain.entity.Product;
import com.example.shop.domain.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findByCategory(Category category, Pageable pageable);

    @Query("SELECT DISTINCT p FROM Product p LEFT JOIN p.variants v " +
            "WHERE (COALESCE(:categoryIds, NULL) IS NULL OR p.category.id IN :categoryIds) " +
            "AND (:minPrice IS NULL OR v.price >= :minPrice) " +
            "AND (:maxPrice IS NULL OR v.price <= :maxPrice) " +
            "AND (:isActive IS NULL OR p.isActive = :isActive)")
    Page<Product> findByFilters(
            @Param("categoryIds") List<Long> categoryIds,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("isActive") Boolean isActive,
            Pageable pageable);

    boolean existsBySlug(String slug);

    Optional<Product> findBySlug(String slug);

    @Query(value = "SELECT * FROM products WHERE is_active = true ORDER BY RAND() LIMIT 4", nativeQuery = true)
    List<Product> findFeaturedProducts();
}
