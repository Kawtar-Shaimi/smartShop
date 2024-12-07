package com.demo.smartShop.service;

import com.demo.smartShop.dto.response.UserDTO;

import javax.servlet.http.HttpSession;

public interface AuthService {

    UserDTO login(String username, String password, HttpSession session);

    void logout(HttpSession session);

    UserDTO getCurrentUser(HttpSession session);
}
