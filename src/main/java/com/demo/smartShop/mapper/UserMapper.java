package com.demo.smartShop.mapper;

import com.demo.smartShop.dto.UserDTO;
import com.demo.smartShop.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "password", ignore = true) // Don't expose password in DTO
    @Mapping(source = "client.id", target = "clientId")
    UserDTO toDTO(User user);

    @Mapping(source = "clientId", target = "client.id")
    User toEntity(UserDTO userDTO);
}
