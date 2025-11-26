package com.demo.smartShop.service.auth;

import com.demo.smartShop.entity.User;
import com.demo.smartShop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final SessionService sessionService;
    private final PasswordService passwordService;

    public boolean login(String username, String password) {
        User user = userRepository.findByUsername(username).orElse(null);

        if (user != null && passwordService.checkPassword(password, user.getPassword())) {
            sessionService.createSession(user);
            return true;
        }
        return false;
    }

    public void logout() {
        sessionService.destroySession();
    }
}