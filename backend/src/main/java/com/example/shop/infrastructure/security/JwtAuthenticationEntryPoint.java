package com.example.shop.infrastructure.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        
        String exceptionType = (String) request.getAttribute("jwt-exception");
        String code = "UNAUTHORIZED";
        String message = "Authentication required.";

        if ("TOKEN_EXPIRED".equals(exceptionType)) {
            code = "TOKEN_EXPIRED";
            message = "Phiên làm việc đã hết hạn. Vui lòng refresh token.";
        } else if ("TOKEN_INVALID".equals(exceptionType)) {
            code = "TOKEN_INVALID";
            message = "Token không hợp lệ hoặc đã bị thay đổi.";
        } else if ("AUTH_ERROR".equals(exceptionType)) {
            code = "AUTH_ERROR";
            message = "Lỗi xác thực hệ thống.";
        }

        Map<String, Object> body = new HashMap<>();
        body.put("success", false);
        body.put("code", code);
        body.put("message", message);
        body.put("timestamp", java.time.Instant.now().toString());

        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getOutputStream(), body);
    }
}
