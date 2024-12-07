package com.demo.smartShop.controller;

import com.demo.smartShop.dto.response.OrderDTO;
import com.demo.smartShop.entity.enums.UserRole;
import com.demo.smartShop.exception.ForbiddenException;
import com.demo.smartShop.exception.UnauthorizedException;
import com.demo.smartShop.service.OrderService;
import com.demo.smartShop.service.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final SessionService sessionService;

    @GetMapping
    public ResponseEntity<Page<OrderDTO>> getAllOrders(Pageable pageable, HttpServletRequest request) {
        // Only ADMIN can view all orders
        requireAdmin(request);
        return ResponseEntity.ok(orderService.getAllOrders(pageable));
    }

    @GetMapping("/my-orders")
    public ResponseEntity<Page<OrderDTO>> getMyOrders(Pageable pageable, HttpServletRequest request) {
        UserRole userRole = getCurrentUserRole(request);

        if (userRole == UserRole.ADMIN) {
            return ResponseEntity.ok(orderService.getAllOrders(pageable));
        }

        Long clientId = getCurrentClientId(request);
        return ResponseEntity.ok(orderService.getOrdersByClientId(clientId, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable Long id, HttpServletRequest request) {
        UserRole currentUserRole = getCurrentUserRole(request);
        OrderDTO order = orderService.getOrderById(id);

        // ADMIN can view any order, CLIENT can only view their own orders
        if (currentUserRole != UserRole.ADMIN) {
            Long currentClientId = getCurrentClientId(request);
            if (!order.getClientId().equals(currentClientId)) {
                throw new ForbiddenException("You can only view your own orders");
            }
        }

        return ResponseEntity.ok(order);
    }

    @PostMapping
    public ResponseEntity<OrderDTO> createOrder(@Valid @RequestBody OrderDTO orderDTO, HttpServletRequest request) {
        // Only ADMIN can create orders
        requireAdmin(request);
        return ResponseEntity.ok(orderService.createOrder(orderDTO));
    }

    @PostMapping("/{id}/confirm")
    public ResponseEntity<OrderDTO> confirmOrder(@PathVariable Long id, HttpServletRequest request) {
        // Only ADMIN can confirm orders
        requireAdmin(request);
        return ResponseEntity.ok(orderService.confirmOrder(id));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<OrderDTO> cancelOrder(@PathVariable Long id, HttpServletRequest request) {
        UserRole currentUserRole = getCurrentUserRole(request);
        OrderDTO order = orderService.getOrderById(id);

        // ADMIN can cancel any order, CLIENT can only cancel their own orders
        if (currentUserRole != UserRole.ADMIN) {
            Long currentClientId = getCurrentClientId(request);
            if (!order.getClientId().equals(currentClientId)) {
                throw new ForbiddenException("You can only cancel your own orders");
            }
        }

        return ResponseEntity.ok(orderService.cancelOrder(id));
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
