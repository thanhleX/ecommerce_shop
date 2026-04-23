package com.example.shop.domain.repository;

import com.example.shop.domain.entity.Blog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface BlogRepository extends JpaRepository<Blog, Long> {
    Page<Blog> findByIsPublishedTrue(Pageable pageable);
    List<Blog> findByIsPublishedTrueAndIsFeaturedTrueOrderByCarouselOrderAsc();
    Optional<Blog> findBySlug(String slug);
    Optional<Blog> findBySlugAndIsPublishedTrue(String slug);
    Page<Blog> findByBlogCategorySlugAndIsPublishedTrue(String categorySlug, Pageable pageable);
}
