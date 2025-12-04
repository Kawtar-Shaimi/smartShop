package com.demo.smartShop.service;

import com.demo.smartShop.dto.PaymentDTO;
import com.demo.smartShop.entity.Order;
import com.demo.smartShop.entity.Payment;
import com.demo.smartShop.entity.enums.OrderStatus;
import com.demo.smartShop.entity.enums.PaymentStatus;
import com.demo.smartShop.entity.enums.PaymentType;
import com.demo.smartShop.mapper.PaymentMapper;
import com.demo.smartShop.repository.OrderRepository;
import com.demo.smartShop.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final PaymentMapper paymentMapper;

    @Transactional
    public PaymentDTO addPayment(PaymentDTO paymentDTO) {
        Order order = orderRepository.findById(paymentDTO.getOrderId())
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new IllegalStateException("Payments can only be added to PENDING orders");
        }

        if (paymentDTO.getType() == PaymentType.ESPECES && paymentDTO.getAmount().compareTo(new BigDecimal("20000")) > 0) {
            throw new IllegalArgumentException("Cash payments cannot exceed 20,000 DH");
        }

        if (paymentDTO.getAmount().compareTo(order.getRemainingAmount()) > 0) {
            throw new IllegalArgumentException("Payment amount exceeds remaining amount");
        }

        Payment payment = paymentMapper.toEntity(paymentDTO);
        payment.setOrder(order);
        payment.setPaymentDate(LocalDateTime.now());

        if (payment.getType() == PaymentType.ESPECES) {
            payment.setStatus(PaymentStatus.ENCAISSE);
            payment.setCashingDate(LocalDateTime.now());
        } else {
            payment.setStatus(PaymentStatus.EN_ATTENTE);
        }


        List<Payment> existingPayments = paymentRepository.findByOrderId(order.getId());
        payment.setPaymentNumber(existingPayments.size() + 1);

        Payment savedPayment = paymentRepository.save(payment);


        order.setRemainingAmount(order.getRemainingAmount().subtract(payment.getAmount()));
        orderRepository.save(order);

        return paymentMapper.toDTO(savedPayment);
    }

    @Transactional
    public PaymentDTO validatePayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new EntityNotFoundException("Payment not found"));

        if (payment.getStatus() == PaymentStatus.ENCAISSE) {
            throw new IllegalStateException("Payment already cashed");
        }

        payment.setStatus(PaymentStatus.ENCAISSE);
        payment.setCashingDate(LocalDateTime.now());

        return paymentMapper.toDTO(paymentRepository.save(payment));
    }

    public List<PaymentDTO> getPaymentsByOrder(Long orderId) {
        return paymentRepository.findByOrderId(orderId).stream()
                .map(paymentMapper::toDTO)
                .collect(Collectors.toList());
    }
}
