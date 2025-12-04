package com.demo.smartShop.dto;

import com.demo.smartShop.entity.enums.CustomerTier;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateClientResponse {
    private Long clientId;
    private String nom;
    private String email;
    private CustomerTier tier;

    // Credentials for login
    private String username;
    private String message;
}
