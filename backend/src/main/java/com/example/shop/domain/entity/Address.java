package com.example.shop.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "addresses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "full_address", columnDefinition = "TEXT")
    private String fullAddress;

    @Column(length = 20)
    private String phone;

    @Column(name = "receiver_name", length = 100)
    private String receiverName;

    @Column(name = "is_default")
    private Boolean isDefault;
}
