package com.demo.smartShop.service.impl;

import com.demo.smartShop.dto.response.UserDTO;
import com.demo.smartShop.entity.User;
import com.demo.smartShop.exception.UnauthorizedException;
import com.demo.smartShop.mapper.UserMapper;
import com.demo.smartShop.repository.UserRepository;
import com.demo.smartShop.service.AuthService;
import com.demo.smartShop.service.SessionService;
import com.demo.smartShop.util.PasswordUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final SessionService sessionService;
    private final UserMapper userMapper;

    @Override
    public UserDTO login(String username, String password, HttpSession session) {
        System.out.println("✓ Login attempt - Username: " + username);

        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            System.out.println("✓ User found: " + user.getUsername());

            // Use BCrypt to verify password
            if (PasswordUtil.verify(password, user.getPassword())) {
                System.out.println("✓ Password match! Login successful");

                // Create session
                sessionService.createSession(session, user);

                // Return DTO
                return userMapper.toDTO(user);
            } else {
                System.out.println("✗ Password mismatch!");
            }
        } else {
            System.out.println("✗ User not found: " + username);
        }
        throw new UnauthorizedException("Invalid username or password");
    }

    @Override
    public void logout(HttpSession session) {
        if (session != null) {
            sessionService.invalidateSession(session);
        }
    }

    @Override
    public UserDTO getCurrentUser(HttpSession session) {
        Long userId = sessionService.getCurrentUserId(session)
                .orElseThrow(() -> new UnauthorizedException("User not authenticated"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UnauthorizedException("User not found"));

        return userMapper.toDTO(user);
    }
}
