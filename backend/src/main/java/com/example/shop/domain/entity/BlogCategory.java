package com.example.shop.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "blog_categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlogCategory extends BaseEntity {

    @Column(length = 100)
    private String name;

    @Column(length = 150)
    private String slug;

    @Column(name = "is_active")
    private Boolean isActive;
}
