package com.demo.smartShop.mapper;

import com.demo.smartShop.dto.response.UserDTO;
import com.demo.smartShop.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(source = "client.id", target = "clientId")
    UserDTO toDTO(User user);

    @Mapping(source = "clientId", target = "client.id")
    @Mapping(target = "password", ignore = true)
    User toEntity(UserDTO userDTO);
}
