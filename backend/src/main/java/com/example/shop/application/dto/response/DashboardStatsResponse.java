package com.example.shop.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class DashboardStatsResponse {
    private long totalOrders;
    private BigDecimal totalRevenue;
    private long totalUsers;
    private long totalProducts;
    private List<RevenueChartData> weeklyRevenue;
    private List<RevenueChartData> monthlyRevenue;
}
