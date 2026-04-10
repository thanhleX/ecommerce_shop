package com.example.shop.domain.repository;

import com.example.shop.domain.entity.Voucher;
import com.example.shop.domain.entity.VoucherUsage;
import com.example.shop.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface VoucherUsageRepository extends JpaRepository<VoucherUsage, Long> {
    long countByVoucher(Voucher voucher);
    long countByVoucherAndUser(Voucher voucher, User user);
    Optional<VoucherUsage> findByOrderId(Long orderId);
}
