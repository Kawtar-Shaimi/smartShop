package com.demo.smartShop.service;

import com.demo.smartShop.dto.request.CreateClientRequest;
import com.demo.smartShop.dto.request.UpdateClientRequest;
import com.demo.smartShop.dto.response.ClientDTO;
import com.demo.smartShop.dto.response.OrderDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface ClientService {
    ClientDTO createClient(CreateClientRequest request);

    ClientDTO getClientById(Long id);

    ClientDTO getClientByUserId(Long userId);

    Page<ClientDTO> getAllClients(Pageable pageable);

    ClientDTO updateClient(Long id, UpdateClientRequest request);

    void deleteClient(Long id);

    void updateClientStats(Long clientId, BigDecimal orderAmount);

    List<OrderDTO> getClientOrders(Long clientId);

    List<ClientDTO> getTopClientsByTotalSpent(int limit);
}
