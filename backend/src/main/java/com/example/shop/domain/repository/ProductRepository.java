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

        @Query(value = "SELECT DISTINCT p.* FROM products p " +
                        "WHERE (:keyword IS NULL OR :keyword = '' " +
                        "   OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
                        "   OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
                        "AND (:categoryIds IS NULL OR p.category_id IN (:categoryIds)) " +
                        "AND (:isActive IS NULL OR p.is_active = :isActive) " +
                        "AND (:minPrice IS NULL OR EXISTS ( " +
                        "    SELECT 1 FROM product_variants v WHERE v.product_id = p.id AND v.price >= :minPrice " +
                        ")) " +
                        "AND (:maxPrice IS NULL OR EXISTS ( " +
                        "    SELECT 1 FROM product_variants v WHERE v.product_id = p.id AND v.price <= :maxPrice " +
                        "))",

                        countQuery = "SELECT COUNT(DISTINCT p.id) FROM products p " +
                                        "WHERE (:keyword IS NULL OR :keyword = '' " +
                                        "   OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
                                        "   OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
                                        "AND (:categoryIds IS NULL OR p.category_id IN (:categoryIds)) " +
                                        "AND (:isActive IS NULL OR p.is_active = :isActive) " +
                                        "AND (:minPrice IS NULL OR EXISTS ( " +
                                        "    SELECT 1 FROM product_variants v WHERE v.product_id = p.id AND v.price >= :minPrice "
                                        +
                                        ")) " +
                                        "AND (:maxPrice IS NULL OR EXISTS ( " +
                                        "    SELECT 1 FROM product_variants v WHERE v.product_id = p.id AND v.price <= :maxPrice "
                                        +
                                        "))",

                        nativeQuery = true)
        Page<Product> findByFilters(
                        @Param("keyword") String keyword,
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
