package com.demo.smartShop.service.impl;

import com.demo.smartShop.entity.User;
import com.demo.smartShop.entity.enums.UserRole;
import com.demo.smartShop.service.SessionService;
import javax.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class SessionServiceImpl implements SessionService {

    @Override
    public void createSession(HttpSession session, User user) {
        session.setAttribute("USER_ID", user.getId());
        session.setAttribute("USERNAME", user.getUsername());
        session.setAttribute("USER_ROLE", user.getRole().name());
        // Store client ID if user has a client (for CLIENT role)
        if (user.getClient() != null) {
            session.setAttribute("CLIENT_ID", user.getClient().getId());
        }
    }

    @Override
    public void invalidateSession(HttpSession session) {
        session.invalidate();
    }

    @Override
    public Optional<Long> getCurrentUserId(HttpSession session) {
        Object userIdObj = session.getAttribute("USER_ID");

        if (userIdObj instanceof Long userId) {
            return Optional.of(userId);
        }

        if (userIdObj instanceof Integer userId) {
            return Optional.of(userId.longValue());
        }

        return Optional.empty();
    }

    @Override
    public Optional<Long> getCurrentClientId(HttpSession session) {
        Object clientIdObj = session.getAttribute("CLIENT_ID");

        if (clientIdObj instanceof Long clientId) {
            return Optional.of(clientId);
        }

        if (clientIdObj instanceof Integer clientId) {
            return Optional.of(clientId.longValue());
        }

        return Optional.empty();
    }

    @Override
    public Optional<UserRole> getCurrentUserRole(HttpSession session) {
        Object roleStringObj = session.getAttribute("USER_ROLE");

        if (roleStringObj instanceof String roleString) {
            try {
                return Optional.of(UserRole.valueOf(roleString));
            } catch (IllegalArgumentException e) {
                log.warn("Invalid session role attribute '{}' found for key: {}", roleString, "USER_ROLE");
            }
        }
        return Optional.empty();
    }

    @Override
    public boolean isAuthenticated(HttpSession session) {
        return getCurrentUserId(session).isPresent();
    }
}