package com.demo.smartShop.mapper;

import com.demo.smartShop.dto.response.PaymentDTO;
import com.demo.smartShop.entity.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PaymentMapper {
    @Mapping(source = "order.id", target = "orderId")
    PaymentDTO toDTO(Payment payment);

    @Mapping(source = "orderId", target = "order.id")
    Payment toEntity(PaymentDTO paymentDTO);
}
