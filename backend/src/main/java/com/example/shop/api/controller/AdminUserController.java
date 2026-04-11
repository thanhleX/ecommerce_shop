package com.example.shop.api.controller;

import com.example.shop.application.dto.common.ApiResponse;
import com.example.shop.application.dto.common.PageResponse;
import com.example.shop.application.dto.response.UserResponse;
import com.example.shop.application.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "Admin User", description = "Admin User management APIs")
public class AdminUserController {

    private final UserService userService;

    @GetMapping("/api/admin/users")
    @PreAuthorize("hasAuthority('user:manage')")
    @Operation(summary = "Get all users (Admin)")
    public ResponseEntity<ApiResponse<PageResponse<UserResponse>>> getAllUsers(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(PageResponse.of(userService.getAllUsers(pageable))));
    }

    @PutMapping("/api/admin/users/{id}/toggle-active")
    @PreAuthorize("hasAuthority('user:manage')")
    @Operation(summary = "Toggle user active status (Admin)")
    public ResponseEntity<ApiResponse<UserResponse>> toggleActive(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(userService.toggleActive(id)));
    }

    @PostMapping("/api/admin/users/staff")
    @PreAuthorize("hasAuthority('user:manage') or hasAuthority('staff:manage')")
    @Operation(summary = "Create an internal staff account with specific roles")
    public ResponseEntity<ApiResponse<UserResponse>> createStaff(@jakarta.validation.Valid @RequestBody com.example.shop.application.dto.request.CreateStaffRequest request) {
        return ResponseEntity.ok(ApiResponse.success(userService.createStaff(request)));
    }

    @PutMapping("/api/admin/users/{id}/roles")
    @PreAuthorize("hasAuthority('user:manage') or hasAuthority('staff:manage')")
    @Operation(summary = "Update user roles (Admin)")
    public ResponseEntity<ApiResponse<UserResponse>> updateRoles(
            @PathVariable Long id, 
            @RequestBody java.util.List<Long> roleIds) {
        return ResponseEntity.ok(ApiResponse.success(userService.updateUserRoles(id, roleIds)));
    }

    @PutMapping("/api/admin/users/{id}/reset-password")
    @PreAuthorize("hasAuthority('user:manage') or hasAuthority('staff:manage')")
    @Operation(summary = "Reset user password to default (Admin)")
    public ResponseEntity<ApiResponse<UserResponse>> resetPassword(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(userService.resetPassword(id)));
    }
}
