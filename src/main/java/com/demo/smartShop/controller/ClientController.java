package com.demo.smartShop.controller;

import com.demo.smartShop.dto.request.CreateClientRequest;
import com.demo.smartShop.dto.request.UpdateClientRequest;
import com.demo.smartShop.dto.response.ClientDTO;
import com.demo.smartShop.dto.response.OrderDTO;
import com.demo.smartShop.dto.response.UserDTO;
import com.demo.smartShop.entity.enums.UserRole;
import com.demo.smartShop.exception.ForbiddenException;
import com.demo.smartShop.exception.UnauthorizedException;
import com.demo.smartShop.service.AuthService;
import com.demo.smartShop.service.ClientService;
import com.demo.smartShop.service.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;
    private final AuthService authService;
    private final SessionService sessionService;

    @PostMapping
    public ResponseEntity<ClientDTO> createClient(@Valid @RequestBody CreateClientRequest request) {
        // Public endpoint - anyone can create a client account
        return new ResponseEntity<>(clientService.createClient(request), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Page<ClientDTO>> getAllClients(Pageable pageable, HttpServletRequest request) {
        requireAdmin(request);
        return ResponseEntity.ok(clientService.getAllClients(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClientDTO> getClientById(@PathVariable Long id, HttpServletRequest request) {
        requireAdmin(request);
        return ResponseEntity.ok(clientService.getClientById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClientDTO> updateClient(@PathVariable Long id,
            @Valid @RequestBody UpdateClientRequest request,
            HttpServletRequest httpRequest) {
        requireAdmin(httpRequest);
        return ResponseEntity.ok(clientService.updateClient(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClient(@PathVariable Long id, HttpServletRequest request) {
        requireAdmin(request);
        clientService.deleteClient(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public ResponseEntity<ClientDTO> getMyProfile(HttpServletRequest request) {
        HttpSession session = request.getSession();
        UserDTO currentUser = authService.getCurrentUser(session);

        if (currentUser.getRole() != UserRole.CLIENT) {
            throw new ForbiddenException("Only clients can access this endpoint");
        }

        return ResponseEntity.ok(clientService.getClientByUserId(currentUser.getId()));
    }

    @GetMapping("/{id}/orders")
    public ResponseEntity<List<OrderDTO>> getClientOrders(@PathVariable Long id, HttpServletRequest request) {
        HttpSession session = request.getSession();
        UserDTO currentUser = authService.getCurrentUser(session);

        // ADMIN can view any client's orders, CLIENT can only view their own
        if (currentUser.getRole() != UserRole.ADMIN) {
            ClientDTO client = clientService.getClientByUserId(currentUser.getId());
            if (!client.getId().equals(id)) {
                throw new ForbiddenException("You can only view your own orders");
            }
        }

        return ResponseEntity.ok(clientService.getClientOrders(id));
    }

    // Helper method for authorization
    private void requireAdmin(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            throw new UnauthorizedException("Authentication required");
        }

        UserRole role = sessionService.getCurrentUserRole(session)
                .orElseThrow(() -> new UnauthorizedException("Authentication required"));

        if (role != UserRole.ADMIN) {
            throw new ForbiddenException("Admin access required");
        }
    }

    @GetMapping("/top")
    public ResponseEntity<List<ClientDTO>> getTopClients(
            @RequestParam(defaultValue = "10") int limit,
            HttpServletRequest request) {
        requireAdmin(request);
        List<ClientDTO> topClients = clientService.getTopClientsByTotalSpent(limit);
        return ResponseEntity.ok(topClients);
    }
}
