package com.demo.smartShop.dto;

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

    @javax.validation.constraints.NotNull(message = "Order ID is required")
    private Long orderId;

    private int paymentNumber;

    @javax.validation.constraints.NotNull(message = "Amount is required")
    @javax.validation.constraints.Positive(message = "Amount must be positive")
    private BigDecimal amount;

    @javax.validation.constraints.NotNull(message = "Payment type is required")
    private PaymentType type;

    private PaymentStatus status;
    private LocalDateTime paymentDate;
    private LocalDateTime cashingDate;

    // Required for CHEQUE and VIREMENT
    private String reference;

    // Required for CHEQUE and VIREMENT
    private String bank;

    // Required for CHEQUE
    private LocalDateTime dueDate;
}
