package com.demo.smartShop.mapper;

import com.demo.smartShop.dto.response.OrderDTO;
import com.demo.smartShop.dto.response.OrderItemDTO;
import com.demo.smartShop.entity.Order;
import com.demo.smartShop.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(source = "client.id", target = "clientId")
    @Mapping(source = "client.nom", target = "clientName")
    OrderDTO toDTO(Order order);

    @Mapping(source = "clientId", target = "client.id")
    Order toEntity(OrderDTO orderDTO);

    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.nom", target = "productName")
    OrderItemDTO toDTO(OrderItem orderItem);

    @Mapping(source = "productId", target = "product.id")
    OrderItem toEntity(OrderItemDTO orderItemDTO);
}
