package com.example.shop.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Lưu các Access Token đã bị thu hồi (đăng xuất).
 * Filter sẽ kiểm tra bảng này trước khi cho phép request đi qua.
 * Cleanup định kỳ lúc 2:00 AM để xóa các token đã hết hạn.
 */
@Entity
@Table(name = "invalidated_tokens", indexes = {
        @Index(name = "idx_invalidated_token", columnList = "token")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvalidatedToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 512)
    private String token;

    @Column(name = "expired_at", nullable = false)
    private LocalDateTime expiredAt;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
