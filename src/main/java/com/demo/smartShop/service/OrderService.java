package com.demo.smartShop.service;

import com.demo.smartShop.dto.response.OrderDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    OrderDTO createOrder(OrderDTO orderDTO);

    OrderDTO confirmOrder(Long orderId);

    OrderDTO cancelOrder(Long orderId);

    OrderDTO rejectOrder(Long orderId);

    Page<OrderDTO> getAllOrders(Pageable pageable);

    Page<OrderDTO> getOrdersByClientId(Long clientId, Pageable pageable);

    OrderDTO getOrderById(Long id);
}
