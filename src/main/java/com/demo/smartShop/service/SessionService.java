package com.demo.smartShop.service;

import com.demo.smartShop.entity.User;
import com.demo.smartShop.entity.enums.UserRole;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SessionService {
    private final HttpSession httpSession;

    private static final String USER_ID = "userId";
    private static final String USERNAME = "username";
    private static final String ROLE = "role";

    public void createSession(User user) {
        httpSession.setAttribute(USER_ID, user.getId());
        httpSession.setAttribute(USERNAME, user.getUsername());
        httpSession.setAttribute(ROLE, user.getRole());
    }

    public void destroySession() {
        httpSession.invalidate();
    }

    public boolean isAuthenticated() {
        return httpSession.getAttribute(USER_ID) != null;
    }

    public boolean hasRole(UserRole requiredRole) {
        UserRole userRole = (UserRole) httpSession.getAttribute(ROLE);
        return userRole != null && userRole.equals(requiredRole);
    }

    public Long getCurrentUserId() {
        return (Long) httpSession.getAttribute(USER_ID);
    }

    public String getCurrentUsername() {
        return (String) httpSession.getAttribute(USERNAME);
    }

    public UserRole getCurrentRole() {
        return (UserRole) httpSession.getAttribute(ROLE);
    }
}