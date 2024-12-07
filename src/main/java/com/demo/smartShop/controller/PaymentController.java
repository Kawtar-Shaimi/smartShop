package com.demo.smartShop.controller;

import com.demo.smartShop.dto.response.OrderDTO;
import com.demo.smartShop.dto.response.PaymentDTO;
import com.demo.smartShop.entity.enums.UserRole;
import com.demo.smartShop.exception.ForbiddenException;
import com.demo.smartShop.exception.UnauthorizedException;
import com.demo.smartShop.service.OrderService;
import com.demo.smartShop.service.PaymentService;
import com.demo.smartShop.service.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final OrderService orderService;
    private final SessionService sessionService;

    @PostMapping
    public ResponseEntity<PaymentDTO> addPayment(@Valid @RequestBody PaymentDTO paymentDTO,
            HttpServletRequest request) {
        UserRole currentUserRole = getCurrentUserRole(request);

        // CLIENT can only add payments to their own orders
        if (currentUserRole != UserRole.ADMIN) {
            Long currentClientId = getCurrentClientId(request);
            OrderDTO order = orderService.getOrderById(paymentDTO.getOrderId());
            if (!order.getClientId().equals(currentClientId)) {
                throw new ForbiddenException("You can only add payments to your own orders");
            }
        }

        return ResponseEntity.ok(paymentService.addPayment(paymentDTO));
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<PaymentDTO>> getPaymentsByOrder(@PathVariable Long orderId, HttpServletRequest request) {
        UserRole currentUserRole = getCurrentUserRole(request);

        // ADMIN can view any order's payments, CLIENT can only view their own order's
        // payments
        if (currentUserRole != UserRole.ADMIN) {
            Long currentClientId = getCurrentClientId(request);
            OrderDTO order = orderService.getOrderById(orderId);
            if (!order.getClientId().equals(currentClientId)) {
                throw new ForbiddenException("You can only view payments for your own orders");
            }
        }

        return ResponseEntity.ok(paymentService.getPaymentsByOrder(orderId));
    }

    @PostMapping("/{id}/validate")
    public ResponseEntity<PaymentDTO> validatePayment(@PathVariable Long id, HttpServletRequest request) {
        // Only ADMIN can validate payments
        requireAdmin(request);
        return ResponseEntity.ok(paymentService.validatePayment(id));
    }

    // Helper methods
    private Long getCurrentClientId(HttpServletRequest request) {
        return sessionService.getCurrentClientId(request.getSession())
                .orElseThrow(() -> new UnauthorizedException("User not authenticated"));
    }

    private UserRole getCurrentUserRole(HttpServletRequest request) {
        return sessionService.getCurrentUserRole(request.getSession())
                .orElseThrow(() -> new UnauthorizedException("User not authenticated"));
    }

    private void requireAdmin(HttpServletRequest request) {
        UserRole role = getCurrentUserRole(request);
        if (role != UserRole.ADMIN) {
            throw new UnauthorizedException("Admin access required");
        }
    }
}
