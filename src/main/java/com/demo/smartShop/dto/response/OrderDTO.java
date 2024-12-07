package com.demo.smartShop.dto.response;

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
    private Long clientId;
    private String clientName;
    private List<OrderItemDTO> items;
    private LocalDateTime date;
    private BigDecimal subTotal;
    private BigDecimal discountAmount;
    private BigDecimal taxAmount;
    private BigDecimal totalAmount;
    private String promoCode;
    private OrderStatus status;
    private BigDecimal remainingAmount;
}
