package com.demo.smartShop.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequestDTO {
    @NotBlank(message = "Username est obligatoire")
    private String username;

    @NotBlank(message = "Password est obligatoire")
    private String password;
}