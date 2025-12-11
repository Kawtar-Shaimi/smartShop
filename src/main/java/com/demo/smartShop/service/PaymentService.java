package com.demo.smartShop.service;

import com.demo.smartShop.dto.response.PaymentDTO;

import java.util.List;

public interface PaymentService {
    PaymentDTO addPayment(PaymentDTO paymentDTO);

    PaymentDTO validatePayment(Long paymentId);

    List<PaymentDTO> getPaymentsByOrder(Long orderId);

    PaymentDTO cancelPayment(Long paymentId);
}
