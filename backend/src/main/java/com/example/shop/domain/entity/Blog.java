package com.example.shop.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "blogs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Blog extends BaseAuditEntity {

    @Column(length = 255)
    private String title;

    @Column(length = 255)
    private String slug;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(columnDefinition = "TEXT")
    private String thumbnail;

    @Column(name = "is_published")
    private Boolean isPublished;

    @Column(name = "is_featured")
    private Boolean isFeatured;

    @Column(name = "carousel_order")
    private Integer carouselOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blog_category_id")
    private BlogCategory blogCategory;
}
