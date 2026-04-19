package com.example.shop.api.controller;

import com.example.shop.application.dto.common.ApiResponse;
import com.example.shop.application.dto.response.DashboardStatsResponse;
import com.example.shop.application.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "Admin Dashboard", description = "Admin Dashboard APIs")
public class AdminDashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/api/admin/dashboard")
    @PreAuthorize("hasAuthority('order:read') or hasAuthority('product:read')")
    @Operation(summary = "Get admin dashboard stats (Admin)")
    public ResponseEntity<ApiResponse<DashboardStatsResponse>> getDashboard() {
        return ResponseEntity.ok(ApiResponse.success(dashboardService.getStats(), "Lấy thông số Dashboard thành công"));
    }
}
