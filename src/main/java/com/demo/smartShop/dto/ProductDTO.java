package com.demo.smartShop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    private Long id;

    @javax.validation.constraints.NotBlank(message = "Name is required")
    private String nom;

    @javax.validation.constraints.NotNull(message = "Price is required")
    @javax.validation.constraints.Positive(message = "Price must be positive")
    private BigDecimal price;

    @javax.validation.constraints.Min(value = 0, message = "Stock cannot be negative")
    private int stock;
}
