package com.demo.smartShop.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePromoCodeRequest {

    @NotBlank(message = "Promo code is required")
    @Pattern(regexp = "PROMO-[A-Z0-9]{4}", message = "Promo code must match format PROMO-XXXX")
    private String code;

    @NotNull(message = "Discount percentage is required")
    @Positive(message = "Discount percentage must be positive")
    private BigDecimal discountPercentage;

    private Integer maxUsage;
}
