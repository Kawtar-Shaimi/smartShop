package com.demo.smartShop.service.auth;  // ⬅️ service.auth

import com.demo.smartShop.entity.User;
import com.demo.smartShop.entity.enums.UserRole;
import com.demo.smartShop.repository.UserRepository;  // ⬅️ Import correct
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final HttpSession httpSession;

    private static final String USER_ID_KEY = "userId";
    private static final String USERNAME_KEY = "username";
    private static final String ROLE_KEY = "role";

    public boolean login(String username, String password) {
        User user = userRepository.findByUsername(username).orElse(null);

        if (user != null && user.getPassword().equals(password)) {
            httpSession.setAttribute(USER_ID_KEY, user.getId());
            httpSession.setAttribute(USERNAME_KEY, user.getUsername());
            httpSession.setAttribute(ROLE_KEY, user.getRole());
            return true;
        }
        return false;
    }

    public void logout() {
        httpSession.invalidate();
    }

    public boolean isAuthenticated() {
        return httpSession.getAttribute(USER_ID_KEY) != null;
    }

    public boolean isAdmin() {
        UserRole role = (UserRole) httpSession.getAttribute(ROLE_KEY);
        return UserRole.ADMIN.equals(role);
    }
}