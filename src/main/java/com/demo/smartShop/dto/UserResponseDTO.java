package com.demo.smartShop.dto;

import com.demo.smartShop.entity.enums.UserRole;
import lombok.Data;

@Data
public class UserResponseDTO {
    private Long id;
    private String username;
    private UserRole role;
}