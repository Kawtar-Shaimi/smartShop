package com.demo.smartShop.service;

import com.demo.smartShop.dto.ClientDTO;
import com.demo.smartShop.dto.CreateClientRequest;
import com.demo.smartShop.dto.CreateClientResponse;
import com.demo.smartShop.entity.Client;
import com.demo.smartShop.entity.User;
import com.demo.smartShop.entity.enums.CustomerTier;
import com.demo.smartShop.entity.enums.UserRole;
import com.demo.smartShop.mapper.ClientMapper;
import com.demo.smartShop.repository.ClientRepository;
import com.demo.smartShop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;
    private final UserRepository userRepository;
    private final ClientMapper clientMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public CreateClientResponse createClientWithAccount(CreateClientRequest request) {
        // Check if email already exists
        if (clientRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }

        // Check if username already exists
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }

        // Create Client
        Client client = Client.builder()
                .nom(request.getNom())
                .email(request.getEmail())
                .tier(CustomerTier.BASIC)
                .totalOrders(0)
                .totalSpent(BigDecimal.ZERO)
                .build();

        Client savedClient = clientRepository.save(client);

        // Create User account linked to Client with HASHED password
        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword())) // ← HASH PASSWORD
                .role(UserRole.CLIENT)
                .client(savedClient)
                .build();

        User savedUser = userRepository.save(user);

        // Log for debugging
        System.out.println(" User created: " + savedUser.getUsername() + " with HASHED password");

        return CreateClientResponse.builder()
                .clientId(savedClient.getId())
                .nom(savedClient.getNom())
                .email(savedClient.getEmail())
                .tier(savedClient.getTier())
                .username(savedUser.getUsername())
                .message("Client and user account created successfully. Use username '" + savedUser.getUsername() + "' to login.")
                .build();
    }

    public ClientDTO createClient(ClientDTO clientDTO) {
        Client client = clientMapper.toEntity(clientDTO);
        // Initialize stats
        client.setTotalOrders(0);
        client.setTotalSpent(BigDecimal.ZERO);
        client.setTier(CustomerTier.BASIC);
        return clientMapper.toDTO(clientRepository.save(client));
    }

    public ClientDTO getClientById(Long id) {
        return clientRepository.findById(id)
                .map(clientMapper::toDTO)
                .orElseThrow(() -> new EntityNotFoundException("Client not found"));
    }

    public java.util.List<ClientDTO> getAllClients() {
        return clientRepository.findAll().stream()
                .map(clientMapper::toDTO)
                .collect(java.util.stream.Collectors.toList());
    }

    @Transactional
    public ClientDTO updateClient(Long id, ClientDTO clientDTO) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Client not found"));

        client.setNom(clientDTO.getNom());
        client.setEmail(clientDTO.getEmail());

        return clientMapper.toDTO(clientRepository.save(client));
    }

    @Transactional
    public void deleteClient(Long id) {
        if (!clientRepository.existsById(id)) {
            throw new EntityNotFoundException("Client not found");
        }
        clientRepository.deleteById(id);
    }

    @Transactional
    public void updateClientStats(Long clientId, BigDecimal orderAmount) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new EntityNotFoundException("Client not found"));

        client.setTotalOrders(client.getTotalOrders() + 1);
        client.setTotalSpent(client.getTotalSpent().add(orderAmount));

        if (client.getFirstOrderDate() == null) {
            client.setFirstOrderDate(LocalDateTime.now());
        }
        client.setLastOrderDate(LocalDateTime.now());

        updateClientTier(client);

        clientRepository.save(client);
    }

    private void updateClientTier(Client client) {
        int orders = client.getTotalOrders();
        BigDecimal spent = client.getTotalSpent();

        if (orders >= 20 || spent.compareTo(new BigDecimal("15000")) >= 0) {
            client.setTier(CustomerTier.PLATINUM);
        } else if (orders >= 10 || spent.compareTo(new BigDecimal("5000")) >= 0) {
            client.setTier(CustomerTier.GOLD);
        } else if (orders >= 3 || spent.compareTo(new BigDecimal("1000")) >= 0) {
            client.setTier(CustomerTier.SILVER);
        } else {
            client.setTier(CustomerTier.BASIC);
        }
    }
}
