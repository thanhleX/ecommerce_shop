package com.example.shop.infrastructure.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;

@Component
@Slf4j
public class JwtTokenProvider {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    /** Access Token: 15 phút (900_000 ms) */
    @Value("${app.jwt.expiration}")
    private long jwtExpirationInMs;

    /** Refresh Token: 7 ngày (604_800_000 ms) */
    @Value("${app.jwt.refresh-expiration}")
    private long jwtRefreshExpirationInMs;

    private SecretKey key;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    // ─────────────────────────────────────────────────────────────
    // Generate Tokens
    // ─────────────────────────────────────────────────────────────

    /**
     * Tạo Access Token từ thông tin Authentication (sau khi đăng nhập thành công)
     */
    public String generateToken(Authentication authentication) {
        String username = authentication.getName();
        return buildToken(username, jwtExpirationInMs);
    }

    /** Tạo Access Token mới từ username (dùng khi refresh) */
    public String generateTokenFromUsername(String username) {
        return buildToken(username, jwtExpirationInMs);
    }

    /**
     * Tạo Refresh Token — là JWT riêng biệt với thời hạn dài hơn.
     * Chứa thêm claim "type=refresh" để phân biệt với access token.
     */
    public String generateRefreshToken(String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtRefreshExpirationInMs);

        return Jwts.builder()
                .setSubject(username)
                .claim("type", "refresh")
                .setId(UUID.randomUUID().toString()) // jti: unique per token
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // ─────────────────────────────────────────────────────────────
    // Extract Info
    // ─────────────────────────────────────────────────────────────

    public String getUsernameFromJWT(String token) {
        return parseClaims(token).getSubject();
    }

    /**
     * Lấy thời điểm hết hạn của token (dùng khi lưu vào bảng invalidated_tokens).
     */
    public LocalDateTime getExpirationFromJWT(String token) {
        Date expiration = parseClaims(token).getExpiration();
        return expiration.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    // ─────────────────────────────────────────────────────────────
    // Validate
    // ─────────────────────────────────────────────────────────────

    public boolean validateToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(authToken);
            return true;
        } catch (SecurityException | MalformedJwtException ex) {
            log.error("Invalid JWT signature");
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty.");
        }
        return false;
    }

    // ─────────────────────────────────────────────────────────────
    // Private Helpers
    // ─────────────────────────────────────────────────────────────

    private String buildToken(String username, long expirationMs) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
