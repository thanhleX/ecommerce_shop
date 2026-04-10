package com.example.shop.domain.repository;

import com.example.shop.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    List<User> findByRole_Name(String roleName);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    long countByRole_NameNot(String roleName);
}
