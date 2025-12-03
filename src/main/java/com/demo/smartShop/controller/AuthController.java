package com.demo.smartShop.controller;

import com.demo.smartShop.dto.UserDTO;
import com.demo.smartShop.entity.User;
import com.demo.smartShop.mapper.UserMapper;
import com.demo.smartShop.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserMapper userMapper;

    @PostMapping("/login")
    public ResponseEntity<UserDTO> login(@RequestBody Map<String, String> credentials, HttpServletRequest request) {
        String username = credentials.get("username");
        String password = credentials.get("password");

        User user = authService.login(username, password);
        UserDTO userDTO = userMapper.toDTO(user);

        HttpSession session = request.getSession();
        session.setAttribute("user", userDTO);

        return ResponseEntity.ok(userDTO);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return ResponseEntity.ok().build();
    }
}
