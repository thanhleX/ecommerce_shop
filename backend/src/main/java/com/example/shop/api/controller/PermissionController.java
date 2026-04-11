package com.example.shop.api.controller;

import com.example.shop.application.dto.common.ApiResponse;
import com.example.shop.application.dto.response.PermissionResponse;
import com.example.shop.application.service.PermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/permissions")
@RequiredArgsConstructor
@Tag(name = "Admin Permissions", description = "Admin Permission Management APIs")
public class PermissionController {

    private final PermissionService permissionService;

    @GetMapping
    @PreAuthorize("hasAuthority('role:read') or hasAuthority('role:manage')")
    @Operation(summary = "Get all system permissions")
    public ResponseEntity<ApiResponse<List<PermissionResponse>>> getAllPermissions() {
        return ResponseEntity.ok(ApiResponse.success(permissionService.getAllPermissions()));
    }
}
