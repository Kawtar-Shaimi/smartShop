package com.demo.smartShop.dto;

import com.demo.smartShop.entity.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
    private Long id;

    @javax.validation.constraints.NotNull(message = "Client ID is required")
    private Long clientId;

    private String clientName;

    @javax.validation.constraints.NotEmpty(message = "Order must have at least one item")
    private List<OrderItemDTO> items;

    private LocalDateTime date;
    private BigDecimal subTotal;
    private BigDecimal discountAmount;
    private BigDecimal taxAmount;
    private BigDecimal totalAmount;

    @javax.validation.constraints.Pattern(regexp = "PROMO-[A-Z0-9]{4}", message = "Invalid promo code format")
    private String promoCode;

    private OrderStatus status;
    private BigDecimal remainingAmount;
}
