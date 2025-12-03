package com.demo.smartShop.config;

import com.demo.smartShop.dto.UserDTO;
import com.demo.smartShop.entity.enums.UserRole;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // Allow public endpoints (Login & Admin Setup)
        if (request.getRequestURI().startsWith("/api/auth/login")) {
            return true;
        }

        // TEMPORARY: Allow admin creation endpoint (DELETE AFTER USE!)
        if (request.getRequestURI().startsWith("/api/setup/create-admin")) {
            return true;
        }

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
            return false;
        }

        UserDTO user = (UserDTO) session.getAttribute("user");
        String uri = request.getRequestURI();
        String method = request.getMethod();

        // Admin can do everything
        if (user.getRole() == UserRole.ADMIN) {
            return true;
        }

        // Client Restrictions
        if (user.getRole() == UserRole.CLIENT) {
            // Can see products (GET)
            if (uri.startsWith("/api/products") && method.equals("GET")) {
                return true;
            }

            // Can see own orders (GET /api/orders/my-orders)
            if (uri.equals("/api/orders/my-orders") && method.equals("GET")) {
                return true;
            }

            // Can see own profile (GET /api/clients/{id})
            if (uri.startsWith("/api/clients/") && method.equals("GET")) {
                // Extract ID from URI
                String idStr = uri.substring(uri.lastIndexOf('/') + 1);
                try {
                    Long id = Long.parseLong(idStr);
                    if (id.equals(user.getClientId())) {
                        return true;
                    }
                } catch (NumberFormatException e) {
                    // Ignore
                }
            }

            // Allow logout
            if (uri.startsWith("/api/auth/logout")) {
                return true;
            }

            // Block everything else (including getAllOrders, createOrder, etc. if Client shouldn't create?)
            // Wait, "CLIENT... NE PEUT PAS créer, modifier, supprimer quoi que ce soit".
            // So CLIENT cannot create orders?
            // Requirement: "CLIENT peut uniquement : Se connecter, Consulter SES PROPRES données, Consulter la liste des produits".
            // "ADMIN peut tout faire : ... Créer des commandes pour n'importe quel client".
            // So CLIENT CANNOT create orders. Only ADMIN creates orders.
            // My implementation allows ADMIN to create orders.
            // So blocking everything else for CLIENT is correct.

            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
            return false;
        }

        return true;
    }
}
