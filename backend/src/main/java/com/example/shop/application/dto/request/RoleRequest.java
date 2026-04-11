package com.example.shop.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import java.util.Set;

@Data
@Builder
public class RoleRequest {
    @NotBlank(message = "Tên vai trò không được để trống")
    private String name;
    
    private Set<String> permissions; // list of permission names or ids. Let's use names to match frontend easier.
}
