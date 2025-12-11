package com.demo.smartShop.service.impl;

import com.demo.smartShop.dto.response.PaymentDTO;
import com.demo.smartShop.entity.Order;
import com.demo.smartShop.entity.Payment;
import com.demo.smartShop.entity.enums.OrderStatus;
import com.demo.smartShop.entity.enums.PaymentStatus;
import com.demo.smartShop.entity.enums.PaymentType;
import com.demo.smartShop.mapper.PaymentMapper;
import com.demo.smartShop.repository.OrderRepository;
import com.demo.smartShop.repository.PaymentRepository;
import com.demo.smartShop.service.PaymentService;
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
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final PaymentMapper paymentMapper;

    @Override
    @Transactional
    public PaymentDTO addPayment(PaymentDTO paymentDTO) {
        Order order = orderRepository.findById(paymentDTO.getOrderId())
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new IllegalStateException("Payments can only be added to PENDING orders");
        }

        if (paymentDTO.getType() == PaymentType.ESPECES
                && paymentDTO.getAmount().compareTo(new BigDecimal("20000")) > 0) {
            throw new IllegalArgumentException("Cash payments cannot exceed 20,000 DH");
        }

        if (paymentDTO.getAmount().compareTo(order.getRemainingAmount()) > 0) {
            throw new IllegalArgumentException("Payment amount exceeds remaining amount");
        }

        // Validate required fields based on payment type
        if (paymentDTO.getType() == PaymentType.CHEQUE || paymentDTO.getType() == PaymentType.VIREMENT) {
            if (paymentDTO.getReference() == null || paymentDTO.getReference().trim().isEmpty()) {
                throw new IllegalArgumentException("Reference is required for " + paymentDTO.getType() + " payments");
            }
            if (paymentDTO.getBank() == null || paymentDTO.getBank().trim().isEmpty()) {
                throw new IllegalArgumentException("Bank is required for " + paymentDTO.getType() + " payments");
            }
        }

        if (paymentDTO.getType() == PaymentType.CHEQUE) {
            if (paymentDTO.getDueDate() == null) {
                throw new IllegalArgumentException("Due date is required for CHEQUE payments");
            }
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

        // Determine payment number
        List<Payment> existingPayments = paymentRepository.findByOrderId(order.getId());
        payment.setPaymentNumber(existingPayments.size() + 1);

        Payment savedPayment = paymentRepository.save(payment);

        order.setRemainingAmount(order.getRemainingAmount().subtract(payment.getAmount()));
        orderRepository.save(order);

        return paymentMapper.toDTO(savedPayment);
    }

    @Override
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

    @Override
    public List<PaymentDTO> getPaymentsByOrder(Long orderId) {
        return paymentRepository.findByOrderId(orderId).stream()
                .map(paymentMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PaymentDTO cancelPayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new EntityNotFoundException("Payment not found"));

        if (payment.getStatus() == PaymentStatus.ENCAISSE) {
            throw new IllegalStateException("Cannot cancel a cashed payment");
        }

        if (payment.getStatus() == PaymentStatus.ANNULEE) {
            throw new IllegalStateException("Payment is already cancelled");
        }

        // Update payment status to ANNULEE
        payment.setStatus(PaymentStatus.ANNULEE);
        Payment cancelledPayment = paymentRepository.save(payment);

        // Restore the remaining amount to the order
        Order order = payment.getOrder();
        order.setRemainingAmount(order.getRemainingAmount().add(payment.getAmount()));
        orderRepository.save(order);

        return paymentMapper.toDTO(cancelledPayment);
    }
}
