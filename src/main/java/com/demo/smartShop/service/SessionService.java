package com.demo.smartShop.service;

import com.demo.smartShop.entity.User;
import com.demo.smartShop.entity.enums.UserRole;

import java.util.Optional;

import javax.servlet.http.HttpSession;

/**
 * Service for managing user sessions and authentication state
 */
public interface SessionService {
    void createSession(HttpSession session, User user);

    void invalidateSession(HttpSession session);

    Optional<Long> getCurrentUserId(HttpSession session);

    Optional<Long> getCurrentClientId(HttpSession session);

    Optional<UserRole> getCurrentUserRole(HttpSession session);

    boolean isAuthenticated(HttpSession session);
}
