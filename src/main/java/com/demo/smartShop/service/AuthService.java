package com.demo.smartShop.service;

import com.demo.smartShop.entity.User;
import com.demo.smartShop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User login(String username, String password) {
        System.out.println(" Login attempt - Username: " + username);

        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            System.out.println(" User found: " + user.getUsername());

            // Use BCrypt to verify password
            if (passwordEncoder.matches(password, user.getPassword())) {
                System.out.println(" Password match! Login successful");
                return user;
            } else {
                System.out.println(" Password mismatch!");
            }
        } else {
            System.out.println(" User not found: " + username);
        }
        throw new RuntimeException("Invalid username or password");
    }
}
