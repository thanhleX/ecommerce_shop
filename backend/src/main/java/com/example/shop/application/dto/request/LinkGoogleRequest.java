package com.example.shop.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LinkGoogleRequest {
    @NotBlank(message = "ID Token là bắt buộc")
    private String idToken;

    @NotBlank(message = "Mật khẩu là bắt buộc để xác thực")
    private String password;
}
