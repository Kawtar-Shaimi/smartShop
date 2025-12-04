package com.demo.smartShop.dto;

import com.demo.smartShop.entity.enums.CustomerTier;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientDTO {
    private Long id;

    @javax.validation.constraints.NotBlank(message = "Name is required")
    private String nom;

    @javax.validation.constraints.Email(message = "Invalid email format")
    @javax.validation.constraints.NotBlank(message = "Email is required")
    private String email;

    private CustomerTier tier;
    private int totalOrders;
    private BigDecimal totalSpent;
    private LocalDateTime firstOrderDate;
    private LocalDateTime lastOrderDate;
}
