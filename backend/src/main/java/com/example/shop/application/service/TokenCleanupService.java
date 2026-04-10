package com.example.shop.application.service;

import com.example.shop.domain.repository.InvalidatedTokenRepository;
import com.example.shop.domain.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Job tự động dọn dẹp các token đã hết hạn khỏi database.
 * Chạy lúc 2:00 AM mỗi ngày để tránh bảng phình to.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TokenCleanupService {

    private final InvalidatedTokenRepository invalidatedTokenRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    /**
     * Xóa các token đã hết hạn.
     * Cron: "0 0 2 * * *" = 2:00 AM mỗi ngày
     */
    @Scheduled(cron = "0 0 2 * * *")
    @Transactional
    public void cleanupExpiredTokens() {
        LocalDateTime now = LocalDateTime.now();

        long deletedInvalidated = invalidatedTokenRepository.count();
        invalidatedTokenRepository.deleteByExpiredAtBefore(now);
        long remainingInvalidated = invalidatedTokenRepository.count();

        long deletedRefresh = refreshTokenRepository.count();
        refreshTokenRepository.deleteByExpiredAtBefore(now);
        long remainingRefresh = refreshTokenRepository.count();

        log.info("[TokenCleanup] Đã xóa {} invalidated tokens và {} refresh tokens hết hạn",
                deletedInvalidated - remainingInvalidated,
                deletedRefresh - remainingRefresh);
    }
}
