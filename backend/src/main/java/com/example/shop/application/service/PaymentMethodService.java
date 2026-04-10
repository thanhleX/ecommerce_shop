package com.example.shop.application.service;

import com.example.shop.application.dto.response.PaymentMethodResponse;
import com.example.shop.application.mapper.OrderMapper;
import com.example.shop.domain.repository.PaymentMethodRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentMethodService {
    private final PaymentMethodRepository paymentMethodRepository;
    private final OrderMapper orderMapper;

    @Transactional(readOnly = true)
    public List<PaymentMethodResponse> getAll() {
        return paymentMethodRepository.findAll().stream()
                .map(orderMapper::toPaymentMethodResponse)
                .toList();
    }
}
