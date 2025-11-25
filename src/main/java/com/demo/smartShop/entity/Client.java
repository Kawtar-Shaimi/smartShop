package com.demo.smartShop.entity;

import com.demo.smartShop.entity.enums.CustomerTier;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "clients")
@Data
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String companyName;

    private String email;
    private String phone;
    private String address;

    @Enumerated(EnumType.STRING)
    private CustomerTier tier = CustomerTier.BASIC;

    private Integer totalOrders = 0;

    @Column(precision = 10, scale = 2)
    private BigDecimal totalSpent = BigDecimal.ZERO;

    private LocalDate firstOrderDate;
    private LocalDate lastOrderDate;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
}