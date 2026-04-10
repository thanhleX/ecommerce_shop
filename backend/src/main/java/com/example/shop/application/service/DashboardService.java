package com.example.shop.application.service;

import com.example.shop.application.dto.response.DashboardStatsResponse;
import com.example.shop.application.dto.response.RevenueChartData;
import com.example.shop.domain.entity.Order;
import com.example.shop.domain.enums.OrderStatus;
import com.example.shop.domain.repository.OrderRepository;
import com.example.shop.domain.repository.ProductRepository;
import com.example.shop.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public DashboardStatsResponse getStats() {
        long totalOrders = orderRepository.count();
        long totalUsers = userRepository.countByRole_NameNot("ADMIN");
        long totalProducts = productRepository.count();

        // 1. Tổng doanh thu (tất cả COMPLETED)
        List<Order> allCompletedOrders = orderRepository.findAll().stream()
                .filter(o -> o.getStatus() == OrderStatus.COMPLETED)
                .collect(Collectors.toList());

        BigDecimal totalRevenue = allCompletedOrders.stream()
                .filter(o -> o.getFinalAmount() != null)
                .map(Order::getFinalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 2. Tính dữ liệu biểu đồ
        List<RevenueChartData> weeklyRevenue = calculateWeeklyRevenue(allCompletedOrders);
        List<RevenueChartData> monthlyRevenue = calculateMonthlyRevenue(allCompletedOrders);

        return DashboardStatsResponse.builder()
                .totalOrders(totalOrders)
                .totalRevenue(totalRevenue)
                .totalUsers(totalUsers)
                .totalProducts(totalProducts)
                .weeklyRevenue(weeklyRevenue)
                .monthlyRevenue(monthlyRevenue)
                .build();
    }

    private List<RevenueChartData> calculateWeeklyRevenue(List<Order> orders) {
        Map<String, BigDecimal> dailyMap = new LinkedHashMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM");
        LocalDateTime now = LocalDateTime.now();

        // Khởi tạo 7 ngày gần nhất với giá trị 0
        for (int i = 6; i >= 0; i--) {
            String label = now.minusDays(i).format(formatter);
            dailyMap.put(label, BigDecimal.ZERO);
        }

        // Điền dữ liệu
        for (Order order : orders) {
            if (order.getOrderDate() == null) continue;
            String label = order.getOrderDate().format(formatter);
            if (dailyMap.containsKey(label)) {
                BigDecimal current = dailyMap.get(label);
                BigDecimal orderAmount = order.getFinalAmount() != null ? order.getFinalAmount() : BigDecimal.ZERO;
                dailyMap.put(label, current.add(orderAmount));
            }
        }

        return dailyMap.entrySet().stream()
                .map(e -> new RevenueChartData(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
    }

    private List<RevenueChartData> calculateMonthlyRevenue(List<Order> orders) {
        Map<String, BigDecimal> monthlyMap = new LinkedHashMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yyyy");
        LocalDateTime now = LocalDateTime.now();

        // Khởi tạo 6 tháng gần nhất với giá trị 0
        for (int i = 5; i >= 0; i--) {
            String label = now.minusMonths(i).format(formatter);
            monthlyMap.put(label, BigDecimal.ZERO);
        }

        // Điền dữ liệu
        for (Order order : orders) {
            if (order.getOrderDate() == null) continue;
            String label = order.getOrderDate().format(formatter);
            if (monthlyMap.containsKey(label)) {
                BigDecimal current = monthlyMap.get(label);
                BigDecimal orderAmount = order.getFinalAmount() != null ? order.getFinalAmount() : BigDecimal.ZERO;
                monthlyMap.put(label, current.add(orderAmount));
            }
        }

        return monthlyMap.entrySet().stream()
                .map(e -> new RevenueChartData(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
    }
}
