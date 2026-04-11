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
        com.example.shop.infrastructure.security.CustomUserDetails userDetails = (com.example.shop.infrastructure.security.CustomUserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        Long id = userDetails.getId();
        java.util.List<String> permissions = authentication.getAuthorities().stream()
                .map(org.springframework.security.core.GrantedAuthority::getAuthority)
                .collect(java.util.stream.Collectors.toList());
        return buildToken(username, id, permissions, jwtExpirationInMs);
    }

    /** Tạo Access Token mới từ username và permissions (dùng khi refresh) */
    public String generateTokenFromUser(com.example.shop.domain.entity.User user) {
        java.util.List<String> permissions = new java.util.ArrayList<>();
        if (user.getRoles() != null) {
            user.getRoles().forEach(role -> {
                permissions.add("ROLE_" + role.getName());
                if (role.getPermissions() != null) {
                    role.getPermissions().forEach(p -> permissions.add(p.getName()));
                }
            });
        }
        return buildToken(user.getUsername(), user.getId(), permissions, jwtExpirationInMs);
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

    public Long getUserIdFromJWT(String token) {
        return parseClaims(token).get("id", Long.class);
    }

    @SuppressWarnings("unchecked")
    public java.util.List<String> getPermissionsFromJWT(String token) {
        return parseClaims(token).get("permissions", java.util.List.class);
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

    private String buildToken(String username, Long id, java.util.List<String> permissions, long expirationMs) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .setSubject(username)
                .claim("id", id)
                .claim("permissions", permissions)
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
