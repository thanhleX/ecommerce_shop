package com.example.shop.domain.repository;

import com.example.shop.domain.entity.Address;
import com.example.shop.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findByUser(User user);
    long countByUser(User user);
}
