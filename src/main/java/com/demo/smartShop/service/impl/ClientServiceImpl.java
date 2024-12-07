package com.demo.smartShop.service.impl;

import com.demo.smartShop.dto.request.CreateClientRequest;
import com.demo.smartShop.dto.request.UpdateClientRequest;
import com.demo.smartShop.dto.response.ClientDTO;
import com.demo.smartShop.dto.response.OrderDTO;
import com.demo.smartShop.entity.Client;
import com.demo.smartShop.entity.User;
import com.demo.smartShop.entity.enums.CustomerTier;
import com.demo.smartShop.entity.enums.UserRole;
import com.demo.smartShop.exception.ResourceNotFoundException;
import com.demo.smartShop.mapper.ClientMapper;
import com.demo.smartShop.mapper.OrderMapper;
import com.demo.smartShop.repository.ClientRepository;
import com.demo.smartShop.repository.OrderRepository;
import com.demo.smartShop.repository.UserRepository;
import com.demo.smartShop.service.ClientService;
import com.demo.smartShop.util.PasswordUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final ClientMapper clientMapper;
    private final OrderMapper orderMapper;

    @Override
    @Transactional
    public ClientDTO createClient(CreateClientRequest request) {
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
                .password(PasswordUtil.hash(request.getPassword()))
                .role(UserRole.CLIENT)
                .client(savedClient)
                .build();

        userRepository.save(user);

        // Return ClientDTO
        return clientMapper.toDTO(savedClient);
    }

    @Override
    public ClientDTO getClientById(Long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client", "id", id));
        return clientMapper.toDTO(client);
    }

    @Override
    public ClientDTO getClientByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        if (user.getClient() == null) {
            throw new ResourceNotFoundException("Client not found for user ID: " + userId);
        }

        return clientMapper.toDTO(user.getClient());
    }

    @Override
    public Page<ClientDTO> getAllClients(Pageable pageable) {
        return clientRepository.findAll(pageable).map(clientMapper::toDTO);
    }

    @Override
    @Transactional
    public ClientDTO updateClient(Long id, UpdateClientRequest request) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client", "id", id));

        client.setNom(request.getNom());
        client.setEmail(request.getEmail());

        return clientMapper.toDTO(clientRepository.save(client));
    }

    @Override
    @Transactional
    public void deleteClient(Long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client", "id", id));

        // Delete associated user account first (if exists) to avoid foreign key
        // constraint
        userRepository.findByUsername(client.getEmail())
                .or(() -> userRepository.findAll().stream()
                        .filter(u -> u.getClient() != null && u.getClient().getId().equals(id))
                        .findFirst())
                .ifPresent(userRepository::delete);

        clientRepository.delete(client);
    }

    @Override
    @Transactional
    public void updateClientStats(Long clientId, BigDecimal orderAmount) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Client", "id", clientId));

        // Update stats
        client.setTotalOrders(client.getTotalOrders() + 1);
        client.setTotalSpent(client.getTotalSpent().add(orderAmount));

        // Update first order date if null
        if (client.getFirstOrderDate() == null) {
            client.setFirstOrderDate(LocalDateTime.now());
        }

        // Always update last order date
        client.setLastOrderDate(LocalDateTime.now());

        // Update tier based on total spent
        updateClientTier(client);

        clientRepository.save(client);
    }

    @Override
    public List<OrderDTO> getClientOrders(Long clientId) {
        // Verify client exists
        if (!clientRepository.existsById(clientId)) {
            throw new ResourceNotFoundException("Client", "id", clientId);
        }

        return orderRepository.findByClientId(clientId, Pageable.unpaged()).stream()
                .map(orderMapper::toDTO)
                .collect(Collectors.toList());
    }

    private void updateClientTier(Client client) {
        BigDecimal totalSpent = client.getTotalSpent();

        if (totalSpent.compareTo(new BigDecimal("5000")) >= 0) {
            client.setTier(CustomerTier.PLATINUM);
        } else if (totalSpent.compareTo(new BigDecimal("2000")) >= 0) {
            client.setTier(CustomerTier.GOLD);
        } else if (totalSpent.compareTo(new BigDecimal("500")) >= 0) {
            client.setTier(CustomerTier.SILVER);
        } else {
            client.setTier(CustomerTier.BASIC);
        }
    }
}
