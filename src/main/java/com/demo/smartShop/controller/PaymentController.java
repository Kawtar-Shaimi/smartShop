package com.demo.smartShop.controller;

import com.demo.smartShop.dto.PaymentDTO;
import com.demo.smartShop.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<PaymentDTO> addPayment(@Valid @RequestBody PaymentDTO paymentDTO) {
        return ResponseEntity.ok(paymentService.addPayment(paymentDTO));
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<PaymentDTO>> getPaymentsByOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(paymentService.getPaymentsByOrder(orderId));
    }

    @PostMapping("/{id}/validate")
    public ResponseEntity<PaymentDTO> validatePayment(@PathVariable Long id) {
        return ResponseEntity.ok(paymentService.validatePayment(id));
    }
}
