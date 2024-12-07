package com.demo.smartShop.dto.response;

import com.demo.smartShop.entity.enums.PaymentStatus;
import com.demo.smartShop.entity.enums.PaymentType;
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
public class PaymentDTO {
    private Long id;
    private Long orderId;
    private int paymentNumber;
    private BigDecimal amount;
    private PaymentType type;
    private PaymentStatus status;
    private LocalDateTime paymentDate;
    private LocalDateTime cashingDate;
    private String reference;
    private String bank;
    private LocalDateTime dueDate;
}
