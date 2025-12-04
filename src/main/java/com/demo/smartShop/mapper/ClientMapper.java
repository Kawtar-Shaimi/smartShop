package com.demo.smartShop.mapper;

import com.demo.smartShop.dto.ClientDTO;
import com.demo.smartShop.entity.Client;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ClientMapper {
    ClientDTO toDTO(Client client);
    Client toEntity(ClientDTO clientDTO);
}
