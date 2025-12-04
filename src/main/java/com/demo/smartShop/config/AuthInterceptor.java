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
        String uri = request.getRequestURI();
        String method = request.getMethod();

        // Allow public endpoints
        if (isPublicEndpoint(uri)) {
            return true;
        }

        // Check authentication
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
            return false;
        }

        UserDTO user = (UserDTO) session.getAttribute("user");

        // ADMIN has full access
        if (user.getRole() == UserRole.ADMIN) {
            return true;
        }

        // CLIENT has limited access
        if (user.getRole() == UserRole.CLIENT) {
            return handleClientAccess(uri, method, user, response);
        }

        response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
        return false;
    }

    /**
     * Check if endpoint is public (no authentication required)
     */
    private boolean isPublicEndpoint(String uri) {
        return uri.startsWith("/api/auth/login") ||
                uri.startsWith("/api/setup/create-admin");
    }

    /**
     * Handle CLIENT role access control
     * CLIENT can only:
     * - View products (GET /api/products)
     * - View own orders (GET /api/orders/my-orders)
     * - View own profile (GET /api/clients/{id})
     * - Logout
     */
    private boolean handleClientAccess(String uri, String method, UserDTO user, HttpServletResponse response) throws Exception {
        // Allow logout
        if (uri.startsWith("/api/auth/logout")) {
            return true;
        }

        // Allow viewing products (read-only)
        if (uri.startsWith("/api/products") && method.equals("GET")) {
            return true;
        }

        // Allow viewing own orders
        if (uri.equals("/api/orders/my-orders") && method.equals("GET")) {
            return true;
        }

        // Allow viewing own profile only
        if (uri.startsWith("/api/clients/") && method.equals("GET")) {
            String idStr = uri.substring(uri.lastIndexOf('/') + 1);
            try {
                Long requestedId = Long.parseLong(idStr);
                if (requestedId.equals(user.getClientId())) {
                    return true;
                }
            } catch (NumberFormatException e) {
                // Invalid ID format, deny access
            }
        }

        // Deny all other access for CLIENT
        response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
        return false;
    }
}
