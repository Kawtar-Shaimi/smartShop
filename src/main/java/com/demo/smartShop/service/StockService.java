package com.demo.smartShop.service;

import com.demo.smartShop.dto.request.OrderItemRequest;

import java.util.List;

public interface StockService {

    boolean isStockAvailable(Long productId, int quantity);

    void decrementStock(Long productId, int quantity);

    void incrementStock(Long productId, int quantity);

    void validateStockForOrder(List<OrderItemRequest> items);
}
