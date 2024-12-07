package com.demo.smartShop.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Pattern;
import java.math.BigDecimal;

@Entity
@Table(name = "promo_codes")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromoCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Pattern(regexp = "PROMO-[A-Z0-9]{4}")
    @Column(unique = true, nullable = false)
    private String code;

    @Column(name = "discount_percentage", nullable = false)
    private BigDecimal discountPercentage;

    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;

    @Column(name = "max_usage")
    private Integer maxUsage;

    @Column(name = "current_usage", nullable = false)
    @Builder.Default
    private int currentUsage = 0;

    public boolean hasRemainingUsage() {
        if (maxUsage == null) {
            return true; // Unlimited usage
        }
        return currentUsage < maxUsage;
    }
}
