package com.demo.smartShop.mapper;

import com.demo.smartShop.entity.User;
import com.demo.smartShop.dto.UserResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserResponseDTO toDto(User user);
}