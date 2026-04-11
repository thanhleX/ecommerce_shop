package com.example.shop.application.service;

import com.example.shop.application.dto.request.ChangePasswordRequest;
import com.example.shop.application.dto.request.LoginRequest;
import com.example.shop.application.dto.request.RefreshTokenRequest;
import com.example.shop.application.dto.request.RegisterRequest;
import com.example.shop.application.dto.response.AuthResponse;
import com.example.shop.domain.entity.Cart;
import com.example.shop.domain.entity.InvalidatedToken;
import com.example.shop.domain.entity.RefreshToken;
import com.example.shop.domain.entity.Role;
import com.example.shop.domain.entity.User;
import com.example.shop.domain.exception.AppException;
import com.example.shop.domain.exception.ErrorCode;
import com.example.shop.domain.repository.CartRepository;
import com.example.shop.domain.repository.InvalidatedTokenRepository;
import com.example.shop.domain.repository.RefreshTokenRepository;
import com.example.shop.domain.repository.RoleRepository;
import com.example.shop.domain.repository.UserRepository;
import com.example.shop.infrastructure.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final CartRepository cartRepository;
    private final InvalidatedTokenRepository invalidatedTokenRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${app.jwt.refresh-expiration}")
    private long refreshExpirationMs;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new AppException(ErrorCode.DUPLICATE_USERNAME);
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.DUPLICATE_EMAIL);
        }
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new AppException(ErrorCode.CONFIRM_PASSWORD_NOT_MATCH);
        }

        Role userRole = roleRepository.findByName("CUSTOMER")
                .orElseThrow(() -> new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION));

        java.util.Set<Role> roles = new java.util.HashSet<>();
        roles.add(userRole);

        User user = User.builder()
                .username(request.getUsername())
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(roles)
                .isActive(true)
                .build();

        User savedUser = userRepository.save(user);

        // Tự động tạo giỏ hàng khi đăng ký
        Cart cart = Cart.builder().user(savedUser).build();
        cartRepository.save(cart);

        // Đăng nhập tự động sau khi đăng ký
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String accessToken = tokenProvider.generateToken(authentication);
        String refreshTokenStr = createAndSaveRefreshToken(savedUser);

        return buildAuthResponse(accessToken, refreshTokenStr, savedUser);
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        if (!user.getIsActive()) {
            throw new AppException(ErrorCode.ACCOUNT_DISABLED);
        }

        String accessToken = tokenProvider.generateToken(authentication);

        // Mỗi lần login: xóa refresh token cũ (nếu có) và tạo mới
        refreshTokenRepository.deleteByUserId(user.getId());
        String refreshTokenStr = createAndSaveRefreshToken(user);

        return buildAuthResponse(accessToken, refreshTokenStr, user);
    }

    @Transactional
    public AuthResponse refreshAccessToken(RefreshTokenRequest request) {
        String requestToken = request.getRefreshToken();

        // 1. Kiểm tra token có trong DB không
        RefreshToken refreshToken = refreshTokenRepository.findByToken(requestToken)
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_REFRESH_TOKEN));

        // 2. Kiểm tra token có hết hạn không
        if (refreshToken.isExpired()) {
            refreshTokenRepository.delete(refreshToken);
            throw new AppException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        // 3. Tạo Access Token mới (không thay Refresh Token)
        User user = refreshToken.getUser();
        String newAccessToken = tokenProvider.generateTokenFromUser(user);

        return AuthResponse.builder()
                .token(newAccessToken)
                .refreshToken(requestToken) // Giữ nguyên refresh token cũ
                .type("Bearer")
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRoles().stream().map(Role::getName).collect(java.util.stream.Collectors.joining(",")))
                .build();
    }

    @Transactional
    public void logout(String bearerToken) {
        if (!StringUtils.hasText(bearerToken) || !bearerToken.startsWith("Bearer ")) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        String accessToken = bearerToken.substring(7);

        // 1. Kiểm tra token hợp lệ trước khi blacklist
        if (!tokenProvider.validateToken(accessToken)) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        // 2. Lưu access token vào bảng blacklist
        LocalDateTime expiredAt = tokenProvider.getExpirationFromJWT(accessToken);
        InvalidatedToken invalidated = InvalidatedToken.builder()
                .token(accessToken)
                .expiredAt(expiredAt)
                .build();
        invalidatedTokenRepository.save(invalidated);

        // 3. Xóa refresh token của user trong DB
        String username = tokenProvider.getUsernameFromJWT(accessToken);
        userRepository.findByUsername(username).ifPresent(user -> refreshTokenRepository.deleteByUserId(user.getId()));

        log.info("User '{}' logged out successfully", username);
    }

    @Transactional
    public void changePassword(ChangePasswordRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new AppException(ErrorCode.INVALID_PASSWORD);
        }

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new AppException(ErrorCode.CONFIRM_PASSWORD_NOT_MATCH);
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        log.info("User '{}' changed password successfully", user.getUsername());
    }

    private String createAndSaveRefreshToken(User user) {
        String tokenValue = tokenProvider.generateRefreshToken(user.getUsername());
        LocalDateTime expiredAt = LocalDateTime.now()
                .plusSeconds(refreshExpirationMs / 1000);

        RefreshToken refreshToken = RefreshToken.builder()
                .token(tokenValue)
                .user(user)
                .expiredAt(expiredAt)
                .build();

        refreshTokenRepository.save(refreshToken);
        return tokenValue;
    }

    private AuthResponse buildAuthResponse(String accessToken, String refreshToken, User user) {
        // Extract all roles to comma-separated string for backwards compatibility payload
        String rolesStr = user.getRoles().stream()
                .map(Role::getName)
                .collect(java.util.stream.Collectors.joining(","));
                
        return AuthResponse.builder()
                .token(accessToken)
                .refreshToken(refreshToken)
                .type("Bearer")
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(rolesStr)
                .build();
    }
}
